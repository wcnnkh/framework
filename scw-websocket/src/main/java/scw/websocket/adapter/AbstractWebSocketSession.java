package scw.websocket.adapter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.Assert;
import scw.lang.Nullable;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.messageing.BinaryMessage;
import scw.messageing.FragmentMessage;
import scw.messageing.Message;
import scw.messageing.TextMessage;
import scw.websocket.CloseStatus;
import scw.websocket.PingMessage;
import scw.websocket.PongMessage;

public abstract class AbstractWebSocketSession<T> implements NativeWebSocketSession {
	protected static final Logger logger = LoggerFactory.getLogger(NativeWebSocketSession.class);

	private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

	@Nullable
	private T nativeSession;

	/**
	 * Create a new instance and associate the given attributes with it.
	 * 
	 * @param attributes
	 *            the attributes from the HTTP handshake to associate with the
	 *            WebSocket session; the provided attributes are copied, the
	 *            original map is not used.
	 */
	public AbstractWebSocketSession(@Nullable Map<String, Object> attributes) {
		if (attributes != null) {
			this.attributes.putAll(attributes);
		}
	}

	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	public T getNativeSession() {
		Assert.state(this.nativeSession != null, "WebSocket session not yet initialized");
		return this.nativeSession;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public <R> R getNativeSession(@Nullable Class<R> requiredType) {
		return (requiredType == null || requiredType.isInstance(this.nativeSession) ? (R) this.nativeSession : null);
	}

	public void initializeNativeSession(T session) {
		Assert.notNull(session, "WebSocket session must not be null");
		this.nativeSession = session;
	}

	protected final void checkNativeSessionInitialized() {
		Assert.state(this.nativeSession != null, "WebSocket session is not yet initialized");
	}
	
	@SuppressWarnings("rawtypes")
	protected boolean isLast(Message<?> message){
		if(message instanceof FragmentMessage){
			return ((FragmentMessage) message).isLast();
		}
		return true;
	}

	public final void sendMessage(Message<?> message) throws IOException {
		checkNativeSessionInitialized();

		if (logger.isTraceEnabled()) {
			logger.trace("Sending " + message + ", " + this);
		}

		if (message instanceof TextMessage) {
			sendTextMessage((TextMessage) message);
		} else if (message instanceof BinaryMessage) {
			sendBinaryMessage((BinaryMessage) message);
		} else if (message instanceof PingMessage) {
			sendPingMessage((PingMessage) message);
		} else if (message instanceof PongMessage) {
			sendPongMessage((PongMessage) message);
		} else {
			throw new IllegalStateException("Unexpected WebSocketMessage type: " + message);
		}
	}

	protected abstract void sendTextMessage(TextMessage message) throws IOException;

	protected abstract void sendBinaryMessage(BinaryMessage message) throws IOException;

	protected abstract void sendPingMessage(PingMessage message) throws IOException;

	protected abstract void sendPongMessage(PongMessage message) throws IOException;

	public final void close() throws IOException {
		close(CloseStatus.NORMAL);
	}

	public final void close(CloseStatus status) throws IOException {
		checkNativeSessionInitialized();
		if (logger.isDebugEnabled()) {
			logger.debug("Closing " + this);
		}
		closeInternal(status);
	}

	protected abstract void closeInternal(CloseStatus status) throws IOException;

	public String toString() {
		if (this.nativeSession != null) {
			return getClass().getSimpleName() + "[id=" + getId() + ", uri=" + getUri() + "]";
		} else {
			return getClass().getSimpleName() + "[nativeSession=null]";
		}
	}

}
