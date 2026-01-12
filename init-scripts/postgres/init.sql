DO
$do$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'res_admin') THEN

      CREATE USER res_admin WITH ENCRYPTED PASSWORD 'Abc@1234';
ELSE
      ALTER USER res_admin WITH ENCRYPTED PASSWORD 'Abc@1234';
END IF;
END
$do$;

GRANT CONNECT ON DATABASE restaurant_db TO res_admin;

CREATE SCHEMA IF NOT EXISTS res_admin AUTHORIZATION res_admin;

GRANT ALL ON SCHEMA res_admin TO res_admin;

ALTER ROLE res_admin SET search_path TO res_admin, public;