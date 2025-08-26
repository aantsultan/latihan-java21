CREATE DATABASE latihan_java21;

CREATE TABLE m_user (
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR (100) NOT NULL,
    first_name VARCHAR (50) NOT NULL,
    last_name VARCHAR (50) NULL,
    email VARCHAR (100) NOT NULL,
    password VARCHAR (300) NOT NULL,
    status TINYINT(1) NOT NULL,
    created_by BIGINT NOT NULL,
    created_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIME,
    modified_by BIGINT NULL,
    modified_datetime TIMESTAMP NULL,
    primary key (user_id),
    unique (username),
    unique (email)
) ENGINE = InnoDB;