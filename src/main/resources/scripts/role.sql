drop table if exists role;

CREATE TABLE role(
   id INTEGER PRIMARY KEY AUTOINCREMENT,
   rolename TEXT NOT NULL UNIQUE,
   user_username TEXT NOT NULL,
   FOREIGN KEY (user_username)
      REFERENCES user (username)
);