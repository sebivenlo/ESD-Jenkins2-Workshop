#!/bin/bash

dropdb --if-exists webshop
createdb -O exam webshop
cat schema.sql | psql -X webshop
