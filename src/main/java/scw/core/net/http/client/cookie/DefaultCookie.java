package scw.core.net.http.client.cookie;

import java.io.Serializable;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import scw.core.utils.StringParseUtils;

public final class DefaultCookie implements Cookie, Serializable {
	private static final long serialVersionUID = 1L;
	private final String cookie;
	private String name;
	private String value;
	private String path;
	private boolean secure;
	private String domain;
	private int maxAge = -1;
	private Date expires;
	private long createTime;

	public DefaultCookie(URL url, String cookie) {
		this.createTime = System.currentTimeMillis();
		this.cookie = cookie;
		String[] arrs = cookie.split("; ");
		for (int i = 0; i < arrs.length; i++) {
			String[] arr = arrs[i].split("=");
			String n = arr[0];
			String v = arr.length == 2 ? arr[1] : null;
			if (i == 0) {
				this.name = n;
				this.value = v;
			} else {
				if (PATH.equals(n)) {
					this.path = v;
				} else if (SECURE.equals(n)) {
					this.secure = StringParseUtils.parseBoolean(v, false);
				} else if (DOMAIN.equals(n)) {
					this.domain = v;
				} else if (MAX_AGE.equals(n)) {
					this.maxAge = Integer.parseInt(v);
				} else if (EXPIRES.equals(n)) {
					SimpleDateFormat format = new SimpleDateFormat();
					try {
						this.expires = format.parse(v);
					} catch (ParseException e) {
					}
				}
			}
		}

		if (path == null) {
			this.path = "/";
		}

		if (domain == null) {
			if (url.getPort() == -1 || url.getPort() == 80) {
				this.domain = url.getHost();
			} else {
				this.domain = url.getHost() + ":" + url.getPort();
			}
		}
	}

	public boolean isEffective() {
		if (name == null) {
			return false;
		}

		if (maxAge == 0) {
			return false;
		}

		if (maxAge > 0) {
			if (System.currentTimeMillis() - createTime < (maxAge * 1000)) {
				return true;
			}
			return false;
		} else if (expires != null) {
			if (expires.getTime() > System.currentTimeMillis()) {
				return false;
			}
			return true;
		}

		return true;
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
		return secure;
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

	public String getCookie() {
		return cookie;
	}

	public long getCreateTime() {
		return createTime;
	}
}
