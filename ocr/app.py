import base64
from flask import Flask, request
import numpy as np
import cv2

from parse_table import parse_image

app = Flask(__name__)


def readb64(data):
    nparr = np.fromstring(base64.b64decode(data), np.uint8)
    img = cv2.imdecode(nparr, 0)
    return img


@app.route("/compute")
def compute():
    if request.data:
        image = readb64(request.data)
        response = parse_image(image)
    return response
