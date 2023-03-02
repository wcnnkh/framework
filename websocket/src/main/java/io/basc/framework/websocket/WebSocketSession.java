package io.basc.framework.websocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.lang.Nullable;
import io.basc.framework.messageing.Message;
import io.basc.framework.messageing.session.Session;

public interface WebSocketSession extends Session {

	String getId();

	@Nullable
	URI getUri();

	HttpHeaders getHandshakeHeaders();

	Map<String, Object> getAttributes();

	@Nullable
	Principal getPrincipal();

	@Nullable
	InetSocketAddress getLocalAddress();

	@Nullable
	InetSocketAddress getRemoteAddress();

	/**
	 * Return the negotiated sub-protocol.
	 * 
	 * @return the protocol identifier, or {@code null} if no protocol was specified
	 *         or negotiated successfully
	 */
	@Nullable
	String getAcceptedProtocol();

	/**
	 * Determine the negotiated extensions.
	 * 
	 * @return the list of extensions, or an empty list if no extension was
	 *         specified or negotiated successfully
	 */
	List<WebSocketExtension> getExtensions();

	void sendMessage(Message<?> message) throws IOException;

	boolean isOpen();

	void close() throws IOException;

	void close(CloseStatus status) throws IOException;
}
