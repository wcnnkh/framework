package io.basc.framework.http.server.cors;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.server.ServerHttpRequest;

class EmptyCors extends Cors {
	@Override
	public void write(ServerHttpRequest request, HttpHeaders headers) {
		// ignore
	}
}