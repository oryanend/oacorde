CREATE TABLE tb_user(
    id uuid PRIMARY KEY ,
    username VARCHAR(40) NOT NULL UNIQUE,
    email VARCHAR(254) NOT NULL UNIQUE,
    password VARCHAR(72) NOT NULL
);