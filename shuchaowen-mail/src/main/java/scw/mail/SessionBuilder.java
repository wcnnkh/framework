package scw.mail;

import java.util.Properties;

import javax.mail.Session;

public class SessionBuilder {
	private String transportProtocol;
	private String host;
	private String port;
	private boolean auth;
	private boolean sslEnable;
	private boolean debug;

	public String getTransportProtocol() {
		return transportProtocol;
	}

	public void setTransportProtocol(String transportProtocol) {
		this.transportProtocol = transportProtocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public boolean isSslEnable() {
		return sslEnable;
	}

	public void setSslEnable(boolean sslEnable) {
		this.sslEnable = sslEnable;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	private String toPropertyKey(String suffix) {
		return "mail." + getTransportProtocol().toLowerCase() + "." + suffix;
	}

	public Properties toProperties() {
		Properties properties = new Properties();
		properties.put("mail.transport.protocol", getTransportProtocol().toLowerCase());// 连接协议
		properties.put(toPropertyKey("host"), getHost());// 主机名
		properties.put(toPropertyKey("port"), getPort());// 端口号
		properties.put(toPropertyKey("auth"), "true");
		properties.put(toPropertyKey("ssl.enable"), "true");// 设置是否使用ssl安全连接
															// ---一般都使用
		properties.put("mail.debug", "true");// 设置是否显示debug信息 true 会在控制台显示相关信息
		return properties;
	}

	public Session builder() {
		Session session = Session.getInstance(toProperties());
		if (isDebug()) {
			session.setDebug(isDebug());
		}
		return session;
	}
}
