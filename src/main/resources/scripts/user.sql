drop table if exists user;

CREATE TABLE user(
   username TEXT PRIMARY KEY NOT NULL UNIQUE,
   password TEXT NOT NULL,
   salt TEXT NOT NULL
);