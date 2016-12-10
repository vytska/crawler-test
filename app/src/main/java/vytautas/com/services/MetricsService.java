package vytautas.com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    @Autowired
    private MetricsEndpoint metricsEndpoint;





}
