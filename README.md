# crawler-test
A practice app. A REST API for crawler write-ahead-log. Crawlers search pages for famous people and report their findings to this service.
### Prerequisites
[Docker](https://www.docker.com/)

[Docker Compose](https://docs.docker.com/compose/)
### Building
docker-compose build

The service uses port 8755, it can be changed in docker-compose.yml 
### Running
docker-compose up

Ir runs the tests adn after that it starts the server. Also runs five crawlers for review data purposes.
The APIs available can be viewed in [http://localhost:8755/swagger-ui.html](http://localhost:8755/swagger-ui.html)

Famous people job management endpoint has the main functionality
