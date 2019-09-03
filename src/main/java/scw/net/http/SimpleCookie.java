package scw.net.http;

import java.io.Serializable;
import java.util.Date;

public class SimpleCookie implements Cookie, Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String value;
	private String path;
	private boolean isSecure;
	private String domain;
	private int maxAge;
	private Date expires;

	public SimpleCookie(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getPath() {
		return path;
	}

	public boolean isSecure() {
		return isSecure;
	}

	public String getDomain() {
		return domain;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public Date getExpires() {
		return expires;
	}
}
