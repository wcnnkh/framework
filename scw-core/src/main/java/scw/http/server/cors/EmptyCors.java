package scw.http.server.cors;

import scw.http.HttpHeaders;

class EmptyCors extends Cors {
	@Override
	public void write(HttpHeaders headers) {
		// ignore
	}
}