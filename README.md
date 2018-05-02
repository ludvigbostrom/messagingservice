# messagingservice

Used a postgresql database. Config to connect in src/main/resources, if not able to connect a local storage is used(Does not save between shutting down the server).

sql command to create needed table is in src/main/sql.

Start by runnning:
```
mvn install
mvn exec:java
```
then go to http://localhost:8080/swagger-ui.html for swagger REST client.
If popup says not finding something.... Just enter http://localhost:8080 or http://localhost:8080/v2/api-docs in prompt(Not really sure which one I used).
