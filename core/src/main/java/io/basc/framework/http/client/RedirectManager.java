package io.basc.framework.http.client;

import java.io.IOException;
import java.net.URI;

import io.basc.framework.http.HttpResponseEntity;

public interface RedirectManager {
	URI getRedirect(ClientHttpResponse response) throws IOException;

	URI getRedirect(HttpResponseEntity<?> responseEntity);
}