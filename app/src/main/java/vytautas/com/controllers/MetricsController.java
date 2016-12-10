package vytautas.com.controllers;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import vytautas.com.services.MetricsService;

import java.util.Map;

@RestController
@Api(description = "Application metrics")
public class MetricsController {

    @Autowired
    private MetricsService metricsService;


    @RequestMapping(path = "/custom-metrics", method = RequestMethod.GET)
    public Map<String, Object> customMetrics() {
        return metricsService.getMetrics();
    }


}
