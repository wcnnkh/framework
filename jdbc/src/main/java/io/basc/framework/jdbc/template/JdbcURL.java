package io.basc.framework.jdbc.template;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * jdbc的url定义
 * 
 * @author wcnnkh
 *
 */
public interface JdbcURL {

	String getProtocol();

	String getHost();

	Integer getProt();

	String getDatabaseNmae();
	
	String getQuery();
	
	public static void main(String[] args) throws URISyntaxException {
		URI url = new URI("jdbc:mysql://localhost:3306/abc");
		System.out.println(url.getRawPath());
		System.out.println(url.getQuery());
		System.out.println(url.getHost());
		System.out.println(url.getFragment());
		System.out.println(url.getUserInfo());
	}
}
