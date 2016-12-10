FROM maven:3.3.9-jdk-8

EXPOSE 8080

WORKDIR /app

CMD mvn clean install

CMD mvn -X clean install exec:java -Dexec.mainClass="vytautas.com.RunApp" -Dexec.classpathScope=test -e



