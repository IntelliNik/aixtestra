#!/usr/bin/env python3
import base64
from flask import Flask, request
import numpy as np
import cv2

from parse_table import parse_image

app = Flask(__name__)


@app.route("/ping")
def ping():
    return "pong"


@app.route("/compute", methods=["POST"])
def compute():
    print("Request received, starting OCR.")

    if request.data:
        data = np.fromstring(request.data, np.uint8)
        image = cv2.imdecode(data, 0)
        response = parse_image(image)
        print("Done, returning response.")
        return response
    else:
        print("Could not parse image data.")
        return "Could not parse image data", 400
