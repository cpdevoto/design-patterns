package com.resolute.okhttp3.simple;

import java.io.IOException;

@FunctionalInterface
interface JsonMapperFunction<R> {

  R apply(String json) throws IOException;

}
