package org.mateus;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.mateus.Constants.*;

public class IncreasedConnectionPool {

    private static final Logger logger = LogManager.getLogger();
    private static final Map<String, List<Long>> requestsLatencyPerURL = new HashMap<>();

    public static void main(String[] args) throws IOException {
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(1000);
        connectionManager.setDefaultMaxPerRoute(250);
        long totalTime = 0;
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build()) {
            requestsLatencyPerURL.clear();
            for (int i = 0; i < EXECUTION_AMOUNT; ++i) {
                totalTime += startRequests(httpClient);
            }
        }
        Percentiles.showPercentiles(requestsLatencyPerURL);
        logger.info(String.format("Total time mean value: %d ms", totalTime / EXECUTION_AMOUNT));
    }

    private static long startRequests(HttpClient httpClient) throws IOException {
        Instant start = Instant.now();
        for (String uri : URIS_TO_GET) {
            executeRequests(httpClient, uri);
        }
        Instant finish = Instant.now();
        long totalTime = Duration.between(start, finish).toMillis();
        logger.info(String.format("Total time: %d ms", totalTime));
        return totalTime;
    }

    private static void executeRequests(HttpClient httpClient, String url) throws IOException {
        logger.info(String.format("Sending %d requests to %s", NUMBER_OF_REQUESTS, url));
        Instant requestStartTime = Instant.now();
        final HttpGet httpGet = new HttpGet(url);
        for (int i = 0; i < NUMBER_OF_REQUESTS; i++) {
            httpClient.execute(httpGet, response -> {
                EntityUtils.consumeQuietly(response.getEntity());
                return null;
            });
        }
        Instant requestEndTime = Instant.now();
        long requestTimeElapsed = Duration.between(requestStartTime, requestEndTime).toMillis();
        requestsLatencyPerURL.computeIfAbsent(url, key -> new ArrayList<>()).add(requestTimeElapsed);
        logger.info(String.format("Elapsed time for %d requests: %d ms", NUMBER_OF_REQUESTS, requestTimeElapsed));
    }
}
