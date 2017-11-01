FROM postgres
COPY scripts/dbsetup.sql /docker-entrypoint-initdb.d/
EXPOSE 5432
VOLUME /var/lib/postgresql/9.6/main
