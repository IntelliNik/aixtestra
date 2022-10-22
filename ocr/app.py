#!/usr/bin/env python3
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

@app.route("/ping")
def ping():
    return "pong"

@app.route("/compute", methods=['POST'])
def compute():
    print("Request received, starting OCR.")

    if request.data:
        image = readb64(request.data)
        response = parse_image(image)
        print("Done, returning response.")
        return response
    else:
        print("Could not parse image data.")
        return "Could not parse image data", 400
