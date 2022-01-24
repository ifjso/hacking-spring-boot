package com.js.hackingspringboot.reactive.ch5;

import lombok.Getter;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.data.annotation.Id;

@Getter
public class HttpTraceWrapper {

    @Id
    private String id;

    private HttpTrace httpTrace;

    private HttpTraceWrapper(HttpTrace httpTrace) {
        this.httpTrace = httpTrace;
    }

    public static HttpTraceWrapper from(HttpTrace httpTrace) {
        return new HttpTraceWrapper(httpTrace);
    }
}
