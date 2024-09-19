package org.mateus;

import java.util.List;

public class Constants {
    public static final int NUMBER_OF_REQUESTS = 1000;
    public static final int EXECUTION_AMOUNT = 10;
    public static final String[] URIS_TO_GET = {
            "http://localhost:3000/google",
            "http://localhost:3000/microsoft",
            "http://localhost:3000/amazon",
            "http://localhost:3000/netflix",
    };
    public static final List<Integer> CONNECTION_POOL_SIZES = List.of(200, 400, 600, 800, 1000);
}
