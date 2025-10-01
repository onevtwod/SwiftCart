-- Create expected databases and set role password for local dev
-- This script runs only on first init of the Postgres data volume

-- Ensure the default superuser has the expected password used by services
ALTER USER postgres WITH PASSWORD 'onevtwod';

-- Create service databases owned by postgres
CREATE DATABASE orders OWNER postgres;
CREATE DATABASE swiftcart_inventory OWNER postgres;


