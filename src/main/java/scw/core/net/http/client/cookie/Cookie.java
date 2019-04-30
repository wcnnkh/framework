package scw.core.net.http.client.cookie;

import java.util.Date;

public interface Cookie {
	public static final String PATH = "path";
	public static final String DOMAIN = "domain";
	public static final String MAX_AGE = "max-age";
	public static final String EXPIRES = "expires";
	public static final String SECURE = "secure";

	String getName();

	String getValue();

	/**
	 * 指定了与cookie关联在一起的网页，
	 * 默认情况下，cookie会和创建它的网页以及与这个网页处于同一个目录下的网页和处于该目录的子目录下的网页关联，同时不能用这个属性来确定安全性
	 * 
	 * @return
	 */
	String getPath();

	/**
	 * 它指定了在网络上如何传输cookie值。
	 * 默认情况下，cookie是不安全的，也就是说，他们是通过一个普通的、不安全的http链接传输的。但是如果将cookie标记为安全的，那么它将只在浏览器和服务器通过https或其他安全协议链接是才被传输。这个属性只能保证cookie是保密的
	 * 
	 * @return
	 */
	boolean isSecure();

	/**
	 * 如果没有设置cookie的domain值，该属性的默认值就是创建cookie的网页所在的服务器的主机名
	 * 
	 * @return
	 */
	String getDomain();

	/**
	 * 绝对过期时间
	 * 
	 * @return
	 */
	int getMaxAge();

	/**
	 * 绝对过期时间 这是字符串要可以被转换为时间格式
	 * 
	 * @return
	 */
	Date getExpires();
}
