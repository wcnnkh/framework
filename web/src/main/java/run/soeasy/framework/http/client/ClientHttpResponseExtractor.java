package run.soeasy.framework.http.client;

import java.io.IOException;

import run.soeasy.framework.http.HttpRequest;

@FunctionalInterface
public interface ClientHttpResponseExtractor<T> {
	T execute(HttpRequest request, ClientHttpResponse response) throws IOException;
}
