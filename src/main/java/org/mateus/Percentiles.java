package org.mateus;

import java.util.List;
import java.util.Map;

public class Percentiles {
    public static void showPercentiles(Map<String, List<Long>> requestsLatencies) {
        requestsLatencies.values().parallelStream().forEach(c -> c.sort(Long::compare));
        for (String key : requestsLatencies.keySet()) {
            int p50Index = (int) Math.ceil(0.5d * requestsLatencies.get(key).size());
            int p90Index = (int) Math.ceil(0.9d * requestsLatencies.get(key).size());
            System.out.printf("URL: %s  p50: %d  p90: %d\n", key, requestsLatencies.get(key).get(p50Index), requestsLatencies.get(key).get(p90Index));
        }
    }
}
