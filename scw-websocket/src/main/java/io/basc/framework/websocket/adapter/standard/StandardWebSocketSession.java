package io.basc.framework.websocket.adapter.standard;

import io.basc.framework.core.Assert;
import io.basc.framework.core.utils.CollectionUtils;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.lang.Nullable;
import io.basc.framework.messageing.BinaryMessage;
import io.basc.framework.messageing.TextMessage;
import io.basc.framework.websocket.CloseStatus;
import io.basc.framework.websocket.PingMessage;
import io.basc.framework.websocket.PongMessage;
import io.basc.framework.websocket.WebSocketExtension;
import io.basc.framework.websocket.adapter.AbstractWebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Extension;
import javax.websocket.Session;

public class StandardWebSocketSession extends AbstractWebSocketSession<Session> {

	@Nullable
	private URI uri;

	private final HttpHeaders handshakeHeaders;

	@Nullable
	private String acceptedProtocol;

	@Nullable
	private List<WebSocketExtension> extensions;

	@Nullable
	private Principal user;

	@Nullable
	private final InetSocketAddress localAddress;

	@Nullable
	private final InetSocketAddress remoteAddress;


	/**
	 * Constructor for a standard WebSocket session.
	 * @param headers the headers of the handshake request
	 * @param attributes the attributes from the HTTP handshake to associate with the WebSocket
	 * session; the provided attributes are copied, the original map is not used.
	 * @param localAddress the address on which the request was received
	 * @param remoteAddress the address of the remote client
	 */
	public StandardWebSocketSession(@Nullable HttpHeaders headers, @Nullable Map<String, Object> attributes,
			@Nullable InetSocketAddress localAddress, @Nullable InetSocketAddress remoteAddress) {

		this(headers, attributes, localAddress, remoteAddress, null);
	}

	/**
	 * Constructor that associates a user with the WebSocket session.
	 * @param headers the headers of the handshake request
	 * @param attributes the attributes from the HTTP handshake to associate with the WebSocket session
	 * @param localAddress the address on which the request was received
	 * @param remoteAddress the address of the remote client
	 * @param user the user associated with the session; if {@code null} we'll
	 * fallback on the user available in the underlying WebSocket session
	 */
	public StandardWebSocketSession(@Nullable HttpHeaders headers, @Nullable Map<String, Object> attributes,
			@Nullable InetSocketAddress localAddress, @Nullable InetSocketAddress remoteAddress,
			@Nullable Principal user) {

		super(attributes);
		headers = (headers != null ? headers : new HttpHeaders());
		this.handshakeHeaders = new HttpHeaders(headers);
		this.handshakeHeaders.readyOnly();
		this.user = user;
		this.localAddress = localAddress;
		this.remoteAddress = remoteAddress;
	}


	public String getId() {
		checkNativeSessionInitialized();
		return getNativeSession().getId();
	}

	@Nullable
	public URI getUri() {
		checkNativeSessionInitialized();
		return this.uri;
	}

	public HttpHeaders getHandshakeHeaders() {
		return this.handshakeHeaders;
	}

	public String getAcceptedProtocol() {
		checkNativeSessionInitialized();
		return this.acceptedProtocol;
	}

	public List<WebSocketExtension> getExtensions() {
		Assert.state(this.extensions != null, "WebSocket session is not yet initialized");
		return this.extensions;
	}

	public Principal getPrincipal() {
		return this.user;
	}

	@Nullable
	public InetSocketAddress getLocalAddress() {
		return this.localAddress;
	}

	@Nullable
	public InetSocketAddress getRemoteAddress() {
		return this.remoteAddress;
	}

	public void setTextMessageSizeLimit(int messageSizeLimit) {
		checkNativeSessionInitialized();
		getNativeSession().setMaxTextMessageBufferSize(messageSizeLimit);
	}

	public int getTextMessageSizeLimit() {
		checkNativeSessionInitialized();
		return getNativeSession().getMaxTextMessageBufferSize();
	}

	public void setBinaryMessageSizeLimit(int messageSizeLimit) {
		checkNativeSessionInitialized();
		getNativeSession().setMaxBinaryMessageBufferSize(messageSizeLimit);
	}

	public int getBinaryMessageSizeLimit() {
		checkNativeSessionInitialized();
		return getNativeSession().getMaxBinaryMessageBufferSize();
	}

	public boolean isOpen() {
		return getNativeSession().isOpen();
	}

	public void initializeNativeSession(Session session) {
		super.initializeNativeSession(session);
		this.uri = session.getRequestURI();
		this.acceptedProtocol = session.getNegotiatedSubprotocol();

		List<Extension> standardExtensions = getNativeSession().getNegotiatedExtensions();
		if (!CollectionUtils.isEmpty(standardExtensions)) {
			this.extensions = new ArrayList<WebSocketExtension>(standardExtensions.size());
			for (Extension standardExtension : standardExtensions) {
				this.extensions.add(new StandardToWebSocketExtensionAdapter(standardExtension));
			}
			this.extensions = Collections.unmodifiableList(this.extensions);
		}
		else {
			this.extensions = Collections.emptyList();
		}

		if (this.user == null) {
			this.user = session.getUserPrincipal();
		}
	}

	@Override
	protected void sendTextMessage(TextMessage message) throws IOException {
		getNativeSession().getBasicRemote().sendText(message.getPayload(), isLast(message));
	}

	@Override
	protected void sendBinaryMessage(BinaryMessage message) throws IOException {
		getNativeSession().getBasicRemote().sendBinary(message.getPayload(), isLast(message));
	}

	@Override
	protected void sendPingMessage(PingMessage message) throws IOException {
		getNativeSession().getBasicRemote().sendPing(message.getPayload());
	}

	@Override
	protected void sendPongMessage(PongMessage message) throws IOException {
		getNativeSession().getBasicRemote().sendPong(message.getPayload());
	}

	@Override
	protected void closeInternal(CloseStatus status) throws IOException {
		getNativeSession().close(new CloseReason(CloseCodes.getCloseCode(status.getCode()), status.getReason()));
	}

}
