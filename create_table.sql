CREATE TABLE IF NOT EXISTS Observations (
    id serial PRIMARY KEY,
    tail varchar(10),
    lat real,
    lon real,
    track real,
    alt real,
    src varchar(10),
    dest varchar(10)
);