-- One-time setup for running listing-search-api against Oracle.
-- Run as an admin user (for example SYSTEM) connected to the pluggable
-- database, not the container root. Oracle Free: FREEPDB1. XE: XEPDB1.
--
-- Replace "change-me" with your own password, and give the same value to
-- the app through the LISTINGS_DB_PASSWORD environment variable.

CREATE USER listings IDENTIFIED BY "change-me";

-- Flyway creates its history table and our tables in this schema, and the
-- identity column on LISTINGS needs no grant beyond CREATE TABLE.
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO listings;

ALTER USER listings QUOTA UNLIMITED ON USERS;

-- To start over with a clean schema (drops the user and everything it owns):
-- DROP USER listings CASCADE;