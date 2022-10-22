FROM python:3.10-slim-buster

RUN apt-get update && apt-get install -y python3-opencv tesseract-ocr libtesseract-dev tesseract-ocr-deu

# We copy just the requirements.txt first to leverage Docker cache
COPY ./requirements.txt /app/requirements.txt

WORKDIR /app

RUN pip install -r requirements.txt

COPY . /app

CMD [ "python3", "-m" , "flask", "run", "--host=0.0.0.0"]