package scw.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;

import scw.net.message.InputMessage;

public interface ServerRequest extends InputMessage {
	String getController();
	
	String getRawContentType();

	String getContextPath();

	String getCharacterEncoding();

	BufferedReader getReader() throws IOException;

	/**
	 * Return the address on which the request was received.
	 */
	InetSocketAddress getLocalAddress();

	/**
	 * Return the address of the remote client.
	 */
	InetSocketAddress getRemoteAddress();
}
