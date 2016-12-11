package vytautas.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import vytautas.com.services.CrawlerRunner;

import java.io.IOException;

@Configuration
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan({"vytautas.com.config", "vytautas.com.services", "vytautas.com.controllers"})
public class AppRunner {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(AppRunner.class);

        CrawlerRunner crawlerRunner = new CrawlerRunner();
        crawlerRunner.runFiveCrawlers();
    }
}













