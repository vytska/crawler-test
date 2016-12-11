package vytautas.com.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(
            nickname = "getCustomMetrics",
            value = "Returns a json object of site metrics",
            notes = "Metrics include 'health' and 'uptime' as well as crawler API calls counts ad durations")
    @RequestMapping(path = "/custom-metrics", method = RequestMethod.GET)
    public Map<String, Object> customMetrics() {
        return metricsService.getMetrics();
    }


}
