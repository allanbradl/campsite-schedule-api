# Camp Schedule API

## Technologies used
* JAVA 11
* Spring boot 2.4.0
* Postgres
* H2 (For testing)
* Maven
* Junit 5
* ELK stack for logs
* Docker
* Docker-compose
* Swagger

## How to Run

 * You can see the api endpoints at http://localhost:8080/swagger-ui.html
 * In the swagger endpoint is possible to try. 
 * It's possible to health check the api at `/actuator/health`
 
### Running local with everything on a container :
 `./mvnw clean package` 
 `cd deps`
 `docker-compose build`
 `docker-compose up -d`
 
 * You can edit the docker-compose yml and add a new-relic license key to see/monitoring the api at newrelic.
 * You can see logs on a local kibana at http://localhost:5601 just need to create an index on kibana for be able to 
 look at the logs.

### Running the jar with only postgress running on a docker: 
  `./mvnw clean package` 
  `cd deps`
  `docker-compose up -d teste-postgres-compose` 
  `java -jar target/campsite-schedule-api-0.0.1.jar`
  
### Testing concurrent request to schedule
`./concurrencieTest.sh `
the expected output for one of the requests is {"description":"no availability for 2021-01-11 to 2021-01-13"}
