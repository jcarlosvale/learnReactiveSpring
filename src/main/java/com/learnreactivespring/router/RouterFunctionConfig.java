package com.learnreactivespring.router;

import com.learnreactivespring.handler.SampleHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> route(SampleHandlerFunction handlerFunction) {
        return RouterFunctions
                .route(GET("/functional/flux")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlerFunction::flux)
                .andRoute(
                        GET("/functional/mono")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlerFunction::mono);
    }
}
