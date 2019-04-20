package com.resolute.okhttp3.simple;

import java.util.function.Consumer;

import okhttp3.HttpUrl;

public interface HttpRequestUrlBuilder<R extends AbstractHttpRequest<R>> {

  public R withUrl(String relativePath);

  public R withUrl(String relativePath, Consumer<HttpUrl.Builder> buildUrlHandler);

}
