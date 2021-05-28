package scw.web.cors;

import scw.http.HttpHeaders;
import scw.web.ServerHttpRequest;

class EmptyCors extends Cors {
	@Override
	public void write(ServerHttpRequest request, HttpHeaders headers) {
		// ignore
	}
}