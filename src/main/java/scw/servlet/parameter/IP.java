package scw.servlet.parameter;

import scw.servlet.Request;

public final class IP {
	private Request request;

	public IP(Request request) {
		this.request = request;
	}

	public String getIp() {
		return request.getIP();
	}

	/**
	 * @return the request
	 */
	public Request getRequest() {
		return request;
	}
}
