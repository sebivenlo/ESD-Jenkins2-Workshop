#!/bin/bash
db=webshop
echo $#
if [ $# -ge 1 ]; then
    db=$1
fi
java -jar /home/hom/Downloads/schemaSpy_5.0.0.jar -dp /usr/share/java/postgresql-jdbc4.jar -db ${db} -s public -u exam -p exam -t pgsql -host localhost -o ${db}-schema
