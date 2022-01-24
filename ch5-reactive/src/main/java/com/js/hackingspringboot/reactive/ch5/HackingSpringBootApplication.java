package com.js.hackingspringboot.reactive.ch5;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@SpringBootApplication
public class HackingSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(HackingSpringBootApplication.class, args);
    }

    @Bean
    HttpTraceRepository springDataTraceRepository(HttpTraceWrapperRepository repository) {
        return new SpringDataHttpTraceRepository(repository);
    }

    @Bean
    MappingMongoConverter mappingMongoConverter(MongoMappingContext context) {
        MappingMongoConverter mappingConverter =
                new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, context);
        mappingConverter.setCustomConversions(
                new MongoCustomConversions(Collections.singletonList(CONVERTER)));

        return mappingConverter;
    }

    static Converter<Document, HttpTraceWrapper> CONVERTER = new Converter<>() {

        @Override
        public HttpTraceWrapper convert(Document source) {
            Document httpTrace = source.get("httpTrace", Document.class);
            Document request = httpTrace.get("request", Document.class);
            Document response = httpTrace.get("response", Document.class);

            return HttpTraceWrapper.from(new HttpTrace(
                    new HttpTrace.Request(
                            request.getString("method"),
                            URI.create(request.getString("uri")),
                            request.get("headers", Map.class),
                            null),
                    new HttpTrace.Response(
                            response.getInteger("status"),
                            response.get("headers", Map.class)),
                    httpTrace.getDate("timestamp").toInstant(),
                    null,
                    null,
                    httpTrace.getLong("timeTaken")));
        }
    };
}
