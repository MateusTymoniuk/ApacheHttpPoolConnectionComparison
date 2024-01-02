package org.mateus;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static org.mateus.Constants.NUMBER_OF_REQUESTS;
import static org.mateus.Constants.URIS_TO_GET;

public class ImprovedDefaultClient {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        Instant start = Instant.now();

        for (String uri : URIS_TO_GET) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                executeRequest(httpClient, uri);
            }
        }

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        logger.info(String.format("Total time: %d ms", timeElapsed));
    }

    private static void executeRequest(HttpClient httpClient, String url) throws IOException {
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
        logger.info(String.format("Elapsed time for %d requests: %d ms", NUMBER_OF_REQUESTS, requestTimeElapsed));
    }
}
