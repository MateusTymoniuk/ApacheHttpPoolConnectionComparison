package org.mateus;

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

import static org.mateus.Constants.NUMBER_OF_REQUESTS;
import static org.mateus.Constants.URIS_TO_GET;

public class MultithreadedClient {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws InterruptedException, IOException {
        Instant start = Instant.now();
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(10);
        connectionManager.setMaxTotal(40);
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
        logger.info(String.format("Total time: %d ms", timeElapsed));
    }

    public static class GetRequestThread extends Thread {
        private final CloseableHttpClient httpClient;
        private final HttpGet httpGet;

        public GetRequestThread(final CloseableHttpClient httpClient, final HttpGet httpGet) {
            this.httpClient = httpClient;
            this.httpGet = httpGet;
        }

        public void run() {
            try {
                logger.info(String.format("Sending %d requests to %s", NUMBER_OF_REQUESTS, httpGet.getURI()));
                Instant requestStartTime = Instant.now();

                for (int i = 0; i < NUMBER_OF_REQUESTS; i++) {
                    httpClient.execute(httpGet, response -> {
                        EntityUtils.consumeQuietly(response.getEntity());
                        return null;
                    });
                }

                Instant requestEndTime = Instant.now();
                long requestTimeElapsed = Duration.between(requestStartTime, requestEndTime).toMillis();
                logger.info(String.format("Elapsed time for %d requests: %d ms", NUMBER_OF_REQUESTS, requestTimeElapsed));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
