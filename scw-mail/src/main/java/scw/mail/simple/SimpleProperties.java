package scw.mail.simple;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import scw.beans.annotation.ConfigurationProperties;
import scw.core.utils.StringUtils;
import scw.util.Verify;
import scw.value.AnyValue;
import scw.value.Value;

@ConfigurationProperties(prefix = SimpleProperties.PREFIX)
public class SimpleProperties extends Properties implements Verify {
	private static final long serialVersionUID = 1L;
	public static final String PREFIX = "mail.";
	protected static final String HOST = "host";
	protected static final String USER = "user";
	protected static final String PASSWORD = "password";
	protected static final String DEBUG = "debug";
	protected static final String CHARSET = "charset";
	protected static final String TRANSPORT_PROTOCOL = "transport.protocol";

	public SimpleProperties() {
		super();
	}

	public SimpleProperties(Properties properties) {
		super(properties);
	}

	public boolean isDebug() {
		Object value = get(PREFIX + DEBUG);
		return new AnyValue(value).getAsBooleanValue();
	}

	public void setDebug(boolean debug) {
		put(PREFIX + DEBUG, true);
	}

	public String getTransportProtocol() {
		return getProperty(PREFIX + TRANSPORT_PROTOCOL);
	}

	public void setTransportProtocol(String transportProtocol) {
		setProperty(PREFIX + TRANSPORT_PROTOCOL, transportProtocol);
	}

	public Value getMailProperty(String key) {
		String protocol = getTransportProtocol();
		if (StringUtils.isNotEmpty(protocol)) {
			Object value = get(PREFIX + protocol + "." + key);
			if (value != null) {
				return new AnyValue(value);
			}
		}

		Object value = get(PREFIX + key);
		if (value == null) {
			value = System.getProperty(PREFIX + key);
		}
		return new AnyValue(value);
	}

	public Value setMailProperty(String key, Object value) {
		String protocol = getTransportProtocol();
		Object old;
		if (StringUtils.isNotEmpty(protocol)) {
			old = put(PREFIX + protocol + "." + key, value);
		} else {
			old = put(PREFIX + key, value);
		}
		return new AnyValue(old);
	}

	@Override
	public synchronized Object put(Object key, Object value) {
		if (value == null) {
			return remove(key);
		}

		if ((PREFIX + TRANSPORT_PROTOCOL + ".").equals(key) && value instanceof String) {
			String newtransportProtocol = (String) value;
			String old = getTransportProtocol();
			if (old != null) {
				// 原来存在设置
				String prefix = PREFIX + old + ".";
				Set<Object> oldKeys = new HashSet<Object>(keySet());
				for (Object oldKey : oldKeys) {
					if (oldKey != null && oldKey instanceof String) {
						String keyStr = (String) oldKey;
						if (keyStr.startsWith(prefix)) {
							Object oldValue = get(oldKey);
							String newKey = PREFIX + newtransportProtocol + "." + keyStr.substring(prefix.length());
							remove(oldKey);
							put(newKey, oldValue);
						}
					}
				}
			}
		}
		return super.put(key, value);
	}

	public String getHost() {
		return getMailProperty(HOST).getAsString();
	}

	public void setHost(String host) {
		setMailProperty(HOST, host);
		int index = host.indexOf(".");
		if (index != -1) {
			setTransportProtocol(host.substring(0, index));
		}
	}

	public Boolean getAuth() {
		return getMailProperty("auth").getAsBoolean();
	}

	public void setAuth(Boolean auth) {
		setMailProperty("auth", auth);
	}

	public Boolean getSslEnable() {
		return getMailProperty("ssl.enable").getAsBoolean();
	}

	public void setSslEnable(Boolean sslEnable) {
		setMailProperty("ssl.enable", sslEnable);
	}

	public String getUser() {
		return getMailProperty(USER).getAsString();
	}

	public void setUser(String user) {
		setMailProperty(USER, user);
	}

	public String getPassword() {
		return getMailProperty(PASSWORD).getAsString();
	}

	public void setPassword(String password) {
		setMailProperty(PASSWORD, password);
		if (StringUtils.isNotEmpty(password)) {
			setAuth(true);
		}
	}

	public String getCharset() {
		return getMailProperty(CHARSET).getAsString();
	}

	public void setCharset(String charset) {
		setMailProperty(CHARSET, charset);
	}

	@Override
	public boolean isVerified() {
		return StringUtils.isNotEmpty(getHost());
	}

	@Override
	public synchronized SimpleProperties clone() {
		return new SimpleProperties(this);
	}
}
