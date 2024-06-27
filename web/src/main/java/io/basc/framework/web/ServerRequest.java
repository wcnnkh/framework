package io.basc.framework.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Principal;

import io.basc.framework.net.InputMessage;
import io.basc.framework.util.attribute.EditableAttributes;

public interface ServerRequest extends EditableAttributes<String, Object>, InputMessage {
	BufferedReader getReader() throws IOException;

	InetSocketAddress getLocalAddress();

	InetSocketAddress getRemoteAddress();

	Principal getPrincipal();

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
}
