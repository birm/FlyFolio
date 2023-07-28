FROM python:3-alpine
WORKDIR /var/www
COPY . /var/www

RUN pip install flask
RUN pip install gunicorn
RUN pip install greenlet
RUN pip install gunicorn[eventlet]
ENV FLASK_ENV development

RUN pip install -r requirements.txt
CMD python /var/www/analysis.py

ENV FLASK_APP analysis.py
CMD python -m flask run --host=0.0.0.0 --port=4000