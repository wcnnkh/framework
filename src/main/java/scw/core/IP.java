package scw.core;

public interface IP {
	/**
	 * ipv4
	 */
	public static final String LOCAL = "127.0.0.1";
	
	/**
	 * ipv4
	 */
	public static final String LOCAL_V4 = LOCAL;
	
	/**
	 * ipv6
	 */
	public static final String LOCAL_V6 = "::1";
	
	String getIP();
}