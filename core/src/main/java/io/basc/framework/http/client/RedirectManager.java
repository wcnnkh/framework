package io.basc.framework.http.client;

import java.net.URI;

import io.basc.framework.http.HttpRequest;
import io.basc.framework.http.HttpResponseEntity;

public interface RedirectManager {
	URI getRedirect(HttpRequest request, HttpResponseEntity<?> responseEntity, long deep);
}