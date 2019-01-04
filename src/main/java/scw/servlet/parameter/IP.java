package scw.servlet.parameter;

import java.io.Serializable;

import scw.servlet.Request;

public final class IP implements Serializable {
	private static final long serialVersionUID = 1L;
	private String ip;

	protected IP() {
	};

	public IP(Request request) {
		this.ip = request.getIP();
	}

	public String getIp() {
		return ip;
	}
}
