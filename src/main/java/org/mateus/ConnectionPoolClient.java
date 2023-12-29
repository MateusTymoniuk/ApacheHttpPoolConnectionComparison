package org.mateus;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;

import static org.mateus.Constants.NUMBER_OF_REQUESTS;
import static org.mateus.Constants.URIS_TO_GET;

public class ConnectionPoolClient {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        Instant start = Instant.now();
        final PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create().build();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build()) {
            for (String uri : URIS_TO_GET) {
                executeRequest(httpClient, uri);
            }
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        logger.info("Time elapsed: " + timeElapsed);
    }

    private static void executeRequest(HttpClient httpClient, String url) throws IOException {
        final HttpGet httpGet = new HttpGet(url);
        for (int i = 0; i < NUMBER_OF_REQUESTS; i++) {
            httpClient.execute(httpGet, response -> {
                try {
                    logger.info("Request sent for " + httpGet.getUri());
                    EntityUtils.consumeQuietly(response.getEntity());
                } catch (URISyntaxException e) {
                    logger.error("Failed to parse URI");
                    throw new RuntimeException();
                }
                return null;
            });
        }
    }
}
