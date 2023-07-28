#!/usr/bin/env python3
import requests
import psycopg2
from datetime import datetime
import os
import sched, time

db_host = os.environ.get("db_host","ffa-db")
db_user = os.environ.get("db_user","postgres")
db_pw = os.environ.get("db_password","postgres")
db_db = os.environ.get("db_db","flyfolio")

schedule_interval = int(os.environ.get("interval","60"))

api_key = os.environ.get("api_key", "750f968c0dmsh878b01f782978d7p190c3ejsn8dae6f1e7b1a")

def insert_observations(observations):
    """ insert a new vendor into the vendors table """
    sql = """INSERT INTO Observations(lat, lon, track, alt, src, dest, tail)
             VALUES(%s,%s,%s,%s,%s,%s,%s)"""
    conn = None
    try:
        # connect to the PostgreSQL database
        conn = psycopg2.connect(
            host=db_host,
            database=db_db,
            user=db_user,
            password=db_pw)
        conn.autocommit = True
        cur = conn.cursor()
        cur.executemany(sql, observations)
        conn.commit()
        cur.close()
        print("successfully added ", len(observations), "observations")
    except (Exception, psycopg2.DatabaseError) as error:
        print(error)
    finally:
        if conn is not None:
            conn.close()

def getObservations():
    url = "https://flight-radar1.p.rapidapi.com/flights/list-in-boundary"

    querystring = {"bl_lat":"13.607884","bl_lng":"100.641975","tr_lat":"13.771029","tr_lng":"100.861566","limit":"300"}

    headers = {
        "X-RapidAPI-Key": api_key,
        "X-RapidAPI-Host": "flight-radar1.p.rapidapi.com"
    }

    response = requests.get(url, headers=headers, params=querystring)
    data = response.json()
    m = [(x[2], x[3], x[4], x[5], x[12], x[13], x[14]) for x in data['aircraft']]
    print(('lat', 'lon', 'track', 'alt(??)', 'src', 'dest', 'tail'))
    print(m)
    return m

def main_insert(my_scheduler):
    my_scheduler.enter(schedule_interval, 1, main_insert, (my_scheduler,))
    obs = getObservations()
    insert_observations(obs)

if __name__ == "__main__":
    print("starting with interval", schedule_interval)
    my_scheduler = sched.scheduler(time.time, time.sleep)
    my_scheduler.enter(schedule_interval, 1, main_insert, (my_scheduler,))
    my_scheduler.run()