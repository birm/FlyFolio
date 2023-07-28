#!/usr/bin/env python3
import psycopg2
from datetime import datetime
import os
import statistics
import flask
import json

app = flask.Flask(__name__)

db_host = os.environ.get("db_host","ffa-db")
db_user = os.environ.get("db_user","postgres")
db_pw = os.environ.get("db_password","postgres")
db_db = os.environ.get("db_db","flyfolio")

api_key = os.environ.get("api_key", "750f968c0dmsh878b01f782978d7p190c3ejsn8dae6f1e7b1a")

@app.route("/statistics/<field>")
def field_stats(field):
    """ insert a new vendor into the vendors table """
    sql = """select * from Observations"""
    conn = None
    posMap = {'track':4, 'lat':2, 'lon':3}
    pos = posMap.get(field, False)
    if not pos:
        return flask.Response(json.dumps({"error": "Did not recognize " + field}))
    try:
        # connect to the PostgreSQL database
        conn = psycopg2.connect(
            host=db_host,
            database=db_db,
            user=db_user,
            password=db_pw)
        conn.autocommit = True
        cur = conn.cursor()
        cur.execute(sql)
        d = cur.fetchall()
        data = [x[pos] for x in d]
        res = {'mean': statistics.mean(data), 'std': statistics.stdev(data), 'quantiles': statistics.quantiles(data)}
        print(res)
        cur.close()
        return flask.Response(json.dumps(res))
    except (Exception, psycopg2.DatabaseError) as error:
        print(error)
        return flask.Response(json.dumps({"error": error}))
    finally:
        if conn is not None:
            conn.close()
