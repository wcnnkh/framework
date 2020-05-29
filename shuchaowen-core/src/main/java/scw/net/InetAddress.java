package scw.net;

import java.io.Serializable;
import java.net.InetSocketAddress;

import scw.core.utils.StringUtils;

/**
 * 使用此类的原因是{@see InetSocketAddress#getHostName()}会反查dns
 * 
 * @author shuchaowen
 *
 */
public class InetAddress implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String host;
	private final int port;

	public InetAddress(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public InetSocketAddress toInetSocketAddress() {
		String host = getHost();
		if (StringUtils.isEmpty(host)) {
			return new InetSocketAddress(getPort());
		}
		return new InetSocketAddress(host, getPort());
	}
}
