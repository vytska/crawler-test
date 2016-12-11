## crawler-test
A practice app. A REST API for crawler write-ahead-log. Crawlers search pages for famous people and report their findings to this service.
#### Prerequisites
[Docker](https://www.docker.com/)

[Docker Compose](https://docs.docker.com/compose/)
#### Building

#### Building and running
The service uses port 8755, it can be changed in docker-compose.yml

```docker-compose up```

Ir runs the tests and after that it starts the server. Also runs five crawlers for review data purposes.  
The APIs available can be viewed in [http://localhost:8755/swagger-ui.html](http://localhost:8755/swagger-ui.html)
  
Famous people job management endpoint has the main functionality. For API details see 
[http://localhost:8755/swagger-ui.html#/famous-people-job-controller](http://localhost:8755/swagger-ui.html#/famous-people-job-controller) 

The health status, uptime and metrics can be checked at [http://localhost:8755/custom-metrics](http://localhost:8755/custom-metrics)
it also includes durations and counts of API method calls. Additionally call durations are logged to console. 
The default metric and health calls are still available at [/metrics](http://localhost:8755/metrics) and [/health](http://localhost:8755/health)
