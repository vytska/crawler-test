package vytautas.com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetricsService {

    @Autowired
    private MetricsEndpoint metricsEndpoint;

    @Autowired
    private HealthEndpoint healthEndpoint;

    public Map<String, Object> getMetrics() {
        Map<String, Object> metricsMap = metricsEndpoint.invoke();

        Map<String, Object> filteredMetrics = metricsMap.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("gauge.famous-people-job") || entry.getKey().startsWith("counter.famous-people-job"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        filteredMetrics.put("uptime", metricsMap.get("uptime"));
        filteredMetrics.put("health", healthEndpoint.invoke().getStatus());

        return filteredMetrics;
    }


}
