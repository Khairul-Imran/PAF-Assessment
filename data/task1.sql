-- Write your Task 1 answers in this file

create database bedandbreakfast;
use bedandbreakfast;

create table users (
    email varchar(128) not null,
    name varchar(128) not null,

    primary key (email)
);

create table bookings (
    booking_id char(8) not null,
    listing_id varchar(20) not null,
    duration int not null,
    email varchar(128) not null,

    primary key (booking_id),
    foreign key (email) references users (email)
);

create table reviews (
    id int not null,
    date timestamp not null,
    listing_id varchar(20) not null,
    reviewer_name varchar(64) not null,
    comments text not null,

    primary key (id)
);

-- Batch insert for users.
LOAD DATA INFILE '/Users/khairulimran/VTTP/PAF/PAF-Assessment/paf_assessment_template/bedandbreakfastapp/data/users.csv'
INTO TABLE users 
FIELDS TERMINATED BY ',' 
-- ENCLOSED BY '"'
-- LINES TERMINATED BY '\n'
-- IGNORE 1 ROWS