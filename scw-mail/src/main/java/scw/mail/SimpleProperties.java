package scw.mail;

import java.util.Properties;

public class SimpleProperties extends Properties {
	private static final long serialVersionUID = 1L;
	public static final String HOST = "mail.host";
	public static final String TRANSPORT_PROTOCOL = "mail.transport.protocol";

	public String getHost() {
		return getProperty(HOST);
	}

	public void setHost(String host) {
		setProperty(HOST, host);
	}

	public String getTransportProtocol() {
		return getProperty(TRANSPORT_PROTOCOL);
	}

	public void setTransportProtocol(String transportProtocol) {
		setProperty(TRANSPORT_PROTOCOL, transportProtocol);
	}
}
