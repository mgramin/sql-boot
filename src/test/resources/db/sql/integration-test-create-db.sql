create schema hr;

create table hr.users (
  id            integer primary key,
  first_name    varchar(30),
  last_name     varchar(30),
  email         varchar(50)
);

create table hr.jobs (
  id            integer primary key,
  name          varchar(30),
  description   varchar(100)
);

create table hr.persons (
  id        integer primary key,
  id_job    integer,
  id_user   integer
);



create table hr.countries (
  id            integer primary key,
  name          varchar(50),
  population    integer
);

create table hr.cities (
  id            integer primary key,
  name          varchar(50),
  id_country    integer
);

create table hr.cities_2 (
  id            integer primary key,
  name          varchar(50),
  id_country    integer
);






create schema bookings;

CREATE TABLE bookings.aircrafts (
    aircraft_code character(3) NOT NULL,
    model text NOT NULL,
    range integer NOT NULL,
    CONSTRAINT aircrafts_range_check CHECK ((range > 0))
);

CREATE TABLE bookings.airports (
    airport_code character(3) NOT NULL,
    airport_name text NOT NULL,
    city text NOT NULL,
    longitude double precision NOT NULL,
    latitude double precision NOT NULL,
    timezone text NOT NULL
);

CREATE TABLE bookings.boarding_passes (
    ticket_no character(13) NOT NULL,
    flight_id integer NOT NULL,
    boarding_no integer NOT NULL,
    seat_no character varying(4) NOT NULL
);

CREATE TABLE bookings.bookings (
    book_ref character(6) NOT NULL,
    book_date timestamp NOT NULL,
    total_amount numeric(10,2) NOT NULL
);

CREATE TABLE bookings.flights (
    flight_id integer NOT NULL,
    flight_no character(6) NOT NULL,
    scheduled_departure timestamp NOT NULL,
    scheduled_arrival timestamp NOT NULL,
    departure_airport character(3) NOT NULL,
    arrival_airport character(3) NOT NULL,
    status character varying(20) NOT NULL,
    aircraft_code character(3) NOT NULL,
    actual_departure timestamp ,
    actual_arrival timestamp);