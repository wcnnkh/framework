package run.soeasy.framework.http.server.cors;

import run.soeasy.framework.http.HttpHeaders;
import run.soeasy.framework.http.server.ServerHttpRequest;

class EmptyCors extends Cors {
	@Override
	public void write(ServerHttpRequest request, HttpHeaders headers) {
		// ignore
	}
}