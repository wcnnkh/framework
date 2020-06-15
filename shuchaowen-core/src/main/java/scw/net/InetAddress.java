package scw.net;

import java.io.Serializable;
import java.net.InetSocketAddress;

public interface InetAddress {
	String getHost();

	int getPort();

	InetSocketAddress getInetSocketAddress();

	/**
	 * 使用此类的原因是{@see InetSocketAddress#getHostName()}会反查dns
	 * 
	 * @author shuchaowen
	 *
	 */
	class DefaultInetAddress implements InetAddress, Serializable {
		private static final long serialVersionUID = 1L;
		private final String host;
		private final int port;
		private InetSocketAddress inetSocketAddress;

		public DefaultInetAddress(String host, int port) {
			this(host, port, new InetSocketAddress(host, port));
		}

		public DefaultInetAddress(String host, int port, InetSocketAddress inetSocketAddress) {
			this.host = host;
			this.port = port;
			this.inetSocketAddress = inetSocketAddress;
		}

		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}

		public InetSocketAddress getInetSocketAddress() {
			return inetSocketAddress;
		}

		@Override
		public String toString() {
			return inetSocketAddress.toString();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}

			if (obj == this) {
				return true;
			}

			if (obj instanceof DefaultInetAddress) {
				return ((DefaultInetAddress) obj).inetSocketAddress.equals(inetSocketAddress);
			}

			if (obj instanceof InetAddress) {
				return ((InetAddress) obj).getInetSocketAddress().equals(inetSocketAddress);
			}

			return inetSocketAddress.equals(obj);
		}
		
		@Override
		public int hashCode() {
			return inetSocketAddress.hashCode();
		}
	}
}
