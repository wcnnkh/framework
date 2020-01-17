package scw.mail;

import java.util.Properties;

import javax.mail.Session;

public class SessionBuilder {
	private String transportProtocol;
	private String host;
	private int port;
	private boolean auth;
	private boolean sslEnable;
	private boolean debug;

	public SessionBuilder(String transportProtocol) {
		this.transportProtocol = transportProtocol;
	}

	public final String getTransportProtocol() {
		return transportProtocol;
	}

	public String getHost() {
		return host;
	}

	public SessionBuilder setHost(String host) {
		this.host = host;
		return this;
	}

	public int getPort() {
		return port;
	}

	public SessionBuilder setPort(int port) {
		this.port = port;
		return this;
	}

	public boolean isAuth() {
		return auth;
	}

	public SessionBuilder setAuth(boolean auth) {
		this.auth = auth;
		return this;
	}

	public boolean isSslEnable() {
		return sslEnable;
	}

	public SessionBuilder setSslEnable(boolean sslEnable) {
		this.sslEnable = sslEnable;
		return this;
	}

	public boolean isDebug() {
		return debug;
	}

	public SessionBuilder setDebug(boolean debug) {
		this.debug = debug;
		return this;
	}

	private String toPropertyKey(String suffix) {
		return "mail." + getTransportProtocol().toLowerCase() + "." + suffix;
	}

	public Properties toProperties() {
		Properties properties = new Properties();
		properties.put("mail.transport.protocol", getTransportProtocol().toLowerCase());// 连接协议
		properties.put(toPropertyKey("host"), getHost());// 主机名
		properties.put(toPropertyKey("port"), getPort());// 端口号
		properties.put(toPropertyKey("auth"), isAuth() + "");
		properties.put(toPropertyKey("ssl.enable"), isSslEnable() + "");// 设置是否使用ssl安全连接
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

	public static Session builderDefault(boolean debug) {
		return new SessionBuilder("smtp").setSslEnable(true).setAuth(true).setHost("smtp.qq.com").setDebug(debug)
				.setPort(465).builder();
	}
}
