## Prerequisites

To run this project you will need to have Java 21 on your JAVA_HOME and also [NodeJS](https://nodejs.org/en)
and [json-server](https://github.com/typicode/json-server#getting-started) installed in order to simulate the server in
which the requests will be made to.

This local server was used because when making to many requests to the servers (I was calling `google.com`
and `microsoft.com` at first), the requests started to get throttled and consequently killed
by `429 - Too many requests`. Tests going to this urls can still be made but the amount of calls is limited.

## Run this project

Start json-server by running

```bash
json-server --port 3000 --watch db.json --middlewares ./delay.js
```

Then run any class of the project with the desired amount of requests:

```bash
./mvnw compile exec:java -Dexec.mainClass="org.mateus.DefaultClient"
```

```bash
./mvnw compile exec:java -Dexec.mainClass="org.mateus.ImprovedDefaultClient"
```

```bash
./mvnw compile exec:java -Dexec.mainClass="org.mateus.ConnectionPoolClient"
```

```bash
./mvnw compile exec:java -Dexec.mainClass="org.mateus.MultithreadedClient"
```

## Configuring the parameters

The configurations that can be made in order to test different scenarios are:

- Include delay to the json-server, by changing the `max` variable value on the `getRandomArbitrary()` function.
- Change the number of requests made by changing the `NUMBER_OF_REQUESTS` variable onto the `Constants` class.
- Include more target urls, by adding a new entry to `db.json` file and then including the new path on `URIS_TO_GET`
  under `Constants` class.
