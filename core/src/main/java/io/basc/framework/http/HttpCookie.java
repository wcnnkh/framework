package io.basc.framework.http;

import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.util.StringUtils;

import java.io.Serializable;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Http Cookie
 * 
 * @see javax.servlet.http.Cookie 未支持RFC 2109
 * @author shuchaowen
 *
 */
public class HttpCookie implements Serializable {
	private static final long serialVersionUID = 1L;
	static final String PATH = "path";
	static final String DOMAIN = "domain";
	static final String MAX_AGE = "max-age";
	static final String EXPIRES = "expires";
	static final String SECURE = "secure";

	private String name;
	private String value;
	private String path;
	private boolean secure;
	private String domain;
	private int maxAge = -1;
	private Date expires;
	private boolean readyOnly;

	public HttpCookie(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public HttpCookie(URL url, String cookie) {
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
					this.secure = StringUtils.parseBoolean(v, false);
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

	protected HttpCookie(HttpCookie httpCookie) {
		this.name = httpCookie.name;
		this.value = httpCookie.value;
		this.path = httpCookie.path;
		this.secure = httpCookie.secure;
		this.domain = httpCookie.domain;
		this.maxAge = httpCookie.maxAge;
		this.expires = httpCookie.expires;
	}

	public boolean isReadyOnly() {
		return readyOnly;
	}

	public HttpCookie readyOnly() {
		if (!readyOnly) {
			readyOnly = true;
		}
		return this;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	/**
	 * 指定了与cookie关联在一起的网页，
	 * 默认情况下，cookie会和创建它的网页以及与这个网页处于同一个目录下的网页和处于该目录的子目录下的网页关联，同时不能用这个属性来确定安全性
	 * 
	 * @return
	 */
	public String getPath() {
		return path;
	}

	/**
	 * 它指定了在网络上如何传输cookie值。
	 * 默认情况下，cookie是不安全的，也就是说，他们是通过一个普通的、不安全的http链接传输的。但是如果将cookie标记为安全的，那么它将只在浏览器和服务器通过https或其他安全协议链接是才被传输。这个属性只能保证cookie是保密的
	 * 
	 * @return
	 */
	public boolean isSecure() {
		return secure;
	}

	/**
	 * 如果没有设置cookie的domain值，该属性的默认值就是创建cookie的网页所在的服务器的主机名
	 * 
	 * @return
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * 绝对过期时间
	 * 
	 * @return
	 */
	public int getMaxAge() {
		return maxAge;
	}

	/**
	 * 绝对过期时间 这是字符串要可以被转换为时间格式
	 * 
	 * @return
	 */
	public Date getExpires() {
		if (expires == null && getMaxAge() != -1) {
			return new Date(System.currentTimeMillis() + getMaxAge());
		}
		return expires;
	}

	public HttpCookie setName(String name) {
		if (isReadyOnly()) {
			throw new NotSupportedException("setName");
		}

		this.name = name;
		return this;
	}

	public HttpCookie setValue(String value) {
		if (isReadyOnly()) {
			throw new NotSupportedException("setValue");
		}
		this.value = value;
		return this;
	}

	public HttpCookie setPath(String path) {
		if (isReadyOnly()) {
			throw new NotSupportedException("setPath");
		}
		this.path = path;
		return this;
	}

	public HttpCookie setSecure(boolean secure) {
		if (isReadyOnly()) {
			throw new NotSupportedException("setSecure");
		}
		this.secure = secure;
		return this;
	}

	public HttpCookie setDomain(String domain) {
		if (isReadyOnly()) {
			throw new NotSupportedException("setDomain");
		}
		this.domain = domain;
		return this;
	}

	public HttpCookie setMaxAge(int maxAge) {
		if (isReadyOnly()) {
			throw new NotSupportedException("setMaxAge");
		}
		this.maxAge = maxAge;
		return this;
	}

	public HttpCookie setExpires(Date expires) {
		if (isReadyOnly()) {
			throw new NotSupportedException("setExpires");
		}
		this.expires = expires;
		return this;
	}

	@Override
	public HttpCookie clone() {
		return new HttpCookie(this);
	}

	@Override
	public String toString() {
		return MapperUtils.getFieldFactory().getFields(HttpCookie.class).getValueMap(this).toString();
	}
}
