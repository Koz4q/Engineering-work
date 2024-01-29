package kuzak.kuba.filmweb;

import io.muserver.*;

import io.muserver.MuServer;
import io.muserver.MuServerBuilder;
import io.muserver.handlers.CORSHandlerBuilder;
import io.muserver.rest.CORSConfig;
import io.muserver.rest.CORSConfigBuilder;
import io.muserver.rest.CollectionParameterStrategy;
import io.muserver.rest.RestHandlerBuilder;

public class App {
    public static void main(String[] args) {
        CORSConfig corsConfig = CORSHandlerBuilder.config()
        		.withAllOriginsAllowed()
                .build();

        MuServer server = MuServerBuilder.httpServer()
                .addHandler(Method.GET, "/", (request, response, pathParams) -> {
                    response.write("Hello");
                })
                .addHandler(Method.PUT, "/", (request, response, pathParams) -> {
                    response.write("Hello");
                })
                .addHandler(RestHandlerBuilder.restHandler(new MovieHandler())
                        .withCORS(corsConfig)
                        .withCollectionParameterStrategy(CollectionParameterStrategy.NO_TRANSFORM))
                .addHandler(RestHandlerBuilder.restHandler(new UserHandler())
                        .withCORS(corsConfig)
                        .withCollectionParameterStrategy(CollectionParameterStrategy.NO_TRANSFORM))
                .addHandler(RestHandlerBuilder.restHandler(new ForumHandler())
                        .withCORS(corsConfig)
                        .withCollectionParameterStrategy(CollectionParameterStrategy.NO_TRANSFORM))
                .withHttpPort(9999)
                .start();

        System.out.println("Started server at " + server.uri());
    }
}
