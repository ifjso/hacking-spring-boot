package com.js.hackingspringboot.reactive.ch5;

import java.util.stream.Stream;
import org.springframework.data.repository.Repository;

public interface HttpTraceWrapperRepository extends Repository<HttpTraceWrapper, String> {

    Stream<HttpTraceWrapper> findAll();

    void save(HttpTraceWrapper trace);
}
