package io.basc.framework.web.cors;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.web.ServerHttpRequest;

class EmptyCors extends Cors {
	@Override
	public void write(ServerHttpRequest request, HttpHeaders headers) {
		// ignore
	}
}