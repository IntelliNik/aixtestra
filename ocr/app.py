#!/usr/bin/env python3
import base64
from flask import Flask, request
import numpy as np
import cv2
import logging

from parse_table import parse_image

app = Flask(__name__)
app.logger.setLevel(logging.DEBUG)

def readb64(data):
    nparr = np.fromstring(base64.b64decode(data), np.uint8)
    img = cv2.imdecode(nparr, 0)
    return img


@app.route("/ping")
def ping():
    return "pong"


@app.route("/compute", methods=['POST'])
def compute():
    app.logger.info("Request received, parsing data.")

    if request.data:
        image = readb64(request.data)
        app.logger.info("Starting OCR.")
        response = parse_image(image)
        app.logger.info("Done, returning response.")
        return response
    else:
        app.logger.info("Could not parse image data.")
        return "Could not parse image data", 400
