import cv2
import numpy as np
import pytesseract
import itertools
import re
import uuid
import sys
import json


def parse_file(file):
    image = cv2.imread(file, 0)
    return parse_image(image)


def parse_image(image):
    length = np.array(image).shape[1] // 100

    # Convert image to gray scale
    grey_scale = cv2.threshold(image, 128, 255, cv2.THRESH_BINARY | cv2.THRESH_OTSU)[1]
    grey_scale = 255 - grey_scale

    # Detect horizontal lines
    horizontal_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (length, 1))
    horizontal_detect = cv2.erode(grey_scale, horizontal_kernel, iterations=3)
    hor_lines = cv2.dilate(horizontal_detect, horizontal_kernel, iterations=3)

    # Detect vertical
    vertical_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (1, length))
    vertical_detect = cv2.erode(grey_scale, vertical_kernel, iterations=3)
    ver_lines = cv2.dilate(vertical_detect, vertical_kernel, iterations=3)

    # Get the lines as [row_start, row_end, col_start, col_end]
    hor_table_lines = find_long_horizontal_lines(hor_lines)
    ver_table_lines = [
        [*line[2:], *line[:2]] for line in find_long_horizontal_lines(ver_lines.T)
    ]
    column_count = len(ver_table_lines) - 1
    row_count = len(hor_table_lines) - 1

    def get_row(index):
        """
        Return the start_y and end_y of the row with this index
        """
        return hor_table_lines[index][1] + 1, hor_table_lines[index + 1][0]

    def get_column(index):
        """
        Return the start_x and end_x of the column with this index
        """
        return ver_table_lines[index][3] + 1, ver_table_lines[index + 1][2]

    def parse_cell(
        row_start, column_start, row_end=None, column_end=None, rotate=False
    ):
        start_y, end_y = get_row(row_start)
        start_x, end_x = get_column(column_start)

        if row_end:
            end_y = get_row(row_end)[1]

        if column_end:
            end_x = get_column(column_end)[1]

        cell = image[start_y:end_y, start_x:end_x]

        if rotate:
            cell = cv2.rotate(cell, cv2.ROTATE_90_CLOCKWISE)

        text = pytesseract.image_to_string(cell, lang="deu").strip()

        return text

    def get_simple_features():

        # test precision of feature's name
        def fix_name(name, simple_features):
            if len(simple_features) == 0:
                return name
            for feature_name in simple_features:
                if name in feature_name:
                    return feature_name
            return name

        start_y, end_y = get_row(0)

        simple_features = {}
        simple_feature_values = []

        for col_index in range(2, column_count):
            text = parse_cell(0, col_index)
            features = re.findall("\s*(\S+): (\S+)\s*", text)

            values = {}

            for name, value in features:
                name = fix_name(name, simple_features)
                if name not in simple_features:
                    simple_features[name] = set([value])
                else:
                    simple_features[name].add(value)

                values[name] = value

            simple_feature_values.append(values)

        simple_features = [
            {"name": name, "values": list(value)}
            for name, value in simple_features.items()
        ]

        return simple_features, simple_feature_values

    def check_row_delimiter(row, column):
        # Check if a cell has a row delimiter at the top
        start_x, end_x = get_column(column)

        return hor_table_lines[row][2] < end_x

    def get_option_range():
        # Parse the option range feature
        name = parse_cell(1, 0, 1, 1)

        # Find option range delimiters
        option_range_delimiter_indices = []
        for row_index in range(2, row_count):
            if check_row_delimiter(row_index, 0):
                option_range_delimiter_indices.append(row_index)

        option_range_delimiter_indices.append(row_count)

        # Parse option range names
        option_ranges = []

        for start, end in itertools.pairwise(option_range_delimiter_indices):
            option_ranges.append(
                {
                    "name": parse_cell(start, 0, row_end=end - 1, rotate=True),
                    "values": [parse_cell(row, 1) for row in range(start, end)],
                }
            )

        return name, option_ranges

    simple_features, simple_feature_values = get_simple_features()
    option_range_feature, option_ranges = get_option_range()

    def get_features():
        # Parse features in the format they want
        features = []

        # Put all the simple features in
        for feature in simple_features:
            id = uuid.uuid4()
            name = feature["name"]

            features.append(
                {
                    "id": id,
                    "nameInFormula": name,
                    "optionRanges": [
                        {
                            "id": uuid.uuid4(),
                            "name": name,
                            "options": [
                                {"id": uuid.uuid4(), "name": value}
                                for value in feature["values"]
                            ],
                        }
                    ],
                }
            )

        # Put the one complex feature in
        features.append(
            {
                "id": uuid.uuid4(),
                "nameInFormula": option_range_feature,
                "optionRanges": [
                    {
                        "id": uuid.uuid4(),
                        "name": option_range["name"],
                        "options": [
                            {"id": uuid.uuid4(), "name": value}
                            for value in option_range["values"]
                        ],
                    }
                    for option_range in option_ranges
                ],
            }
        )

        return features

    features = get_features()

    def get_prices():
        prices = []

        # Calculate the prices. Go through all rows/columns.
        for col in range(2, column_count):
            # Values for simple features
            simple_vals = simple_feature_values[col - 2]

            for row in range(2, row_count):
                option_range_index = 0
                row_offset = 2
                while row - row_offset >= len(
                    option_ranges[option_range_index]["values"]
                ):
                    row_offset += len(option_ranges[option_range_index]["values"])
                    option_range_index += 1
                option_index = row - row_offset

                price = parse_cell(row, col)
                price = re.findall("^\D*(\d+)\D*$", price)
                if not price:
                    continue
                price = int(price[0])

                prices.append(
                    {
                        "id": uuid.uuid4(),
                        "value": {
                            "currencyUnit": "EUR",
                            "amountInMinorUnits": price * 100,
                        },
                        "optionSelections": [
                            *[
                                {
                                    "featureId": features[-1]["id"],
                                    "optionId": features[-1]["optionRanges"][
                                        option_range_index
                                    ]["options"][option_index]["id"],
                                }
                            ],
                            *[
                                {
                                    "featureId": features[feature_index]["id"],
                                    "optionId": features[feature_index]["optionRanges"][
                                        0
                                    ]["options"][
                                        simple_feature["values"].index(
                                            simple_vals[simple_feature["name"]]
                                        )
                                    ][
                                        "id"
                                    ],
                                }
                                for feature_index, simple_feature in enumerate(
                                    simple_features
                                )
                            ],
                        ],
                    }
                )

            return prices

    prices = get_prices()

    return {"product": {"features": features, "prices": prices}, "hints": None}


def find_long_horizontal_lines(hor_lines):
    # Find long horizontal lines that. They belong to our table.
    height, width = hor_lines.shape

    # Each entry is [row_start, row_end, col_start, col_end]
    hor_table_lines = []

    # We look for one long (min. half image width) line per row.
    for row in range(height):
        line_start = None

        for column in range(width):
            if hor_lines[row, column]:
                if line_start is None:
                    line_start = column
            else:
                if line_start and (column - line_start) > width / 5:
                    hor_table_lines.append([row, row, line_start, column])

                line_start = None

    # Merge adjacent lines
    i = 0
    while i < len(hor_table_lines) - 1:
        curr = hor_table_lines[i]
        next = hor_table_lines[i + 1]

        if curr[0] + 1 == next[0] and curr[2:] == next[2:]:
            curr[1] += 1
            hor_table_lines.remove(next)

        i += 1

    return hor_table_lines


if __name__ == "__main__":

    class UUIDEncoder(json.JSONEncoder):
        def default(self, obj):
            if isinstance(obj, uuid.UUID):
                # if the obj is uuid, we simply return the value of uuid
                return str(obj)
            return json.JSONEncoder.default(self, obj)

    print(json.dumps(parse_file(sys.argv[1]), cls=UUIDEncoder, indent=2))
