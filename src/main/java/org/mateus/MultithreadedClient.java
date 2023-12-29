package org.mateus;

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

public class MultithreadedClient {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws InterruptedException, IOException {
        Instant start = Instant.now();
        final PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create().build();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build()) {

            // creating threads to perform the GET requests
            GetRequestThread[] threads = new GetRequestThread[URIS_TO_GET.length];

            for (int i = 0; i < URIS_TO_GET.length; i++) {
                final HttpGet httpGet = new HttpGet(URIS_TO_GET[i]);
                threads[i] = new GetRequestThread(httpClient, httpGet);
            }

            for (GetRequestThread thread : threads) {
                thread.start();
            }
            for (GetRequestThread thread : threads) {
                thread.join();
            }
        }

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        logger.info("Time elapsed: " + timeElapsed);
    }

    public static class GetRequestThread extends Thread {
        private final CloseableHttpClient client;
        private final HttpGet get;

        public GetRequestThread(final CloseableHttpClient client, final HttpGet get) {
            this.client = client;
            this.get = get;
        }

        public void run() {
            try {
                for (int i = 0; i < NUMBER_OF_REQUESTS; i++) {
                    logger.info("Request sent for " + get.getUri());
                    client.execute(get, response -> {
                        EntityUtils.consumeQuietly(response.getEntity());
                        return null;
                    });
                }
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
