package io.basc.framework.websocket;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.lang.Nullable;
import io.basc.framework.messageing.BinaryMessage;
import io.basc.framework.messageing.Message;
import io.basc.framework.messageing.TextMessage;
import io.basc.framework.messageing.session.Session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface WebSocketSession extends Session {

	/**
	 * Return a unique session identifier.
	 */
	String getId();

	/**
	 * Return the URI used to open the WebSocket connection.
	 */
	@Nullable
	URI getUri();

	/**
	 * Return the headers used in the handshake request (never {@code null}).
	 */
	HttpHeaders getHandshakeHeaders();

	/**
	 * Return the map with attributes associated with the WebSocket session.
	 * @return a Map with the session attributes (never {@code null})
	 */
	Map<String, Object> getAttributes();

	/**
	 * Return a {@link java.security.Principal} instance containing the name of
	 * the authenticated user.
	 * <p>
	 * If the user has not been authenticated, the method returns
	 * <code>null</code>.
	 */
	@Nullable
	Principal getPrincipal();

	/**
	 * Return the address on which the request was received.
	 */
	@Nullable
	InetSocketAddress getLocalAddress();

	/**
	 * Return the address of the remote client.
	 */
	@Nullable
	InetSocketAddress getRemoteAddress();

	/**
	 * Return the negotiated sub-protocol.
	 * 
	 * @return the protocol identifier, or {@code null} if no protocol was
	 *         specified or negotiated successfully
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

	/**
	 * 根据规定不允许并发发送
	 * <br/>
	 * Send a WebSocket message: either {@link TextMessage} or
	 * {@link BinaryMessage}.
	 *
	 * <p>
	 * <strong>Note:</strong> The underlying standard WebSocket session
	 * (JSR-356) does not allow concurrent sending. Therefore sending must be
	 * synchronized.
	 * 
	 */
	void sendMessage(Message<?> message) throws IOException;

	/**
	 * Return whether the connection is still open.
	 */
	boolean isOpen();

	/**
	 * Close the WebSocket connection with status 1000, i.e. equivalent to:
	 * 
	 * <pre class="code">
	 * session.close(CloseStatus.NORMAL);
	 * </pre>
	 */
	void close() throws IOException;

	/**
	 * Close the WebSocket connection with the given close status.
	 */
	void close(CloseStatus status) throws IOException;
}
