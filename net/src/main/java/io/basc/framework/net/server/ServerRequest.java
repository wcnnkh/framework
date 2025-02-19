package io.basc.framework.net.server;

import java.net.InetSocketAddress;

import io.basc.framework.net.InputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.net.RequestPatternCapable;
import io.basc.framework.util.attribute.EditableAttributes;

public interface ServerRequest
		extends Request, InputMessage, EditableAttributes<String, Object>, RequestPatternCapable {
	public static interface ServerRequestWrapper<W extends ServerRequest> extends ServerRequest, RequestWrapper<W>,
			InputMessageWrapper<W>, EditableAttributesWrapper<String, Object, W>, RequestPatternCapableWrapper<W> {
		@Override
		default InetSocketAddress getLocalAddress() {
			return getSource().getLocalAddress();
		}

		@Override
		default InetSocketAddress getRemoteAddress() {
			return getSource().getRemoteAddress();
		}

		@Override
		default String getProtocol() {
			return getSource().getProtocol();
		}

		@Override
		default String getScheme() {
			return getSource().getScheme();
		}

		@Override
		default boolean isSupportAsyncControl() {
			return getSource().isSupportAsyncControl();
		}

		@Override
		default ServerAsyncControl getAsyncControl(ServerResponse serverResponse) {
			return getSource().getAsyncControl(serverResponse);
		}

		@Override
		default ServerRequestDispatcher getRequestDispatcher(String path) {
			return getSource().getRequestDispatcher(path);
		}
	}

	InetSocketAddress getLocalAddress();

	InetSocketAddress getRemoteAddress();

	/**
	 * Returns the name and version of the protocol the request uses in the form
	 * <i>protocol/majorVersion.minorVersion</i>, for example, HTTP/1.1. For HTTP
	 * servlets, the value returned is the same as the value of the CGI variable
	 * <code>SERVER_PROTOCOL</code>.
	 *
	 * @return a <code>String</code> containing the protocol name and version number
	 */
	String getProtocol();

	/**
	 * Returns the name of the scheme used to make this request, for example,
	 * <code>http</code>, <code>https</code>, or <code>ftp</code>. Different schemes
	 * have different rules for constructing URLs, as noted in RFC 1738.
	 *
	 * @return a <code>String</code> containing the name of the scheme used to make
	 *         this request
	 */
	String getScheme();

	boolean isSupportAsyncControl();

	ServerAsyncControl getAsyncControl(ServerResponse serverResponse);

	ServerRequestDispatcher getRequestDispatcher(String path);
}
