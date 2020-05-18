package scw.websocket.handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import scw.core.Assert;
import scw.http.HttpHeaders;
import scw.messageing.Message;
import scw.websocket.CloseStatus;
import scw.websocket.WebSocketExtension;
import scw.websocket.WebSocketSession;

public class WebSocketSessionDecorator implements WebSocketSession{

	private final WebSocketSession delegate;


	public WebSocketSessionDecorator(WebSocketSession session) {
		Assert.notNull(session, "Delegate WebSocketSessionSession is required");
		this.delegate = session;
	}


	public WebSocketSession getDelegate() {
		return this.delegate;
	}

	public WebSocketSession getLastSession() {
		WebSocketSession result = this.delegate;
		while (result instanceof WebSocketSessionDecorator) {
			result = ((WebSocketSessionDecorator) result).getDelegate();
		}
		return result;
	}

	public static WebSocketSession unwrap(WebSocketSession session) {
		if (session instanceof WebSocketSessionDecorator) {
			return ((WebSocketSessionDecorator) session).getLastSession();
		}
		else {
			return session;
		}
	}

	public String getId() {
		return this.delegate.getId();
	}

	public URI getUri() {
		return this.delegate.getUri();
	}

	public HttpHeaders getHandshakeHeaders() {
		return this.delegate.getHandshakeHeaders();
	}

	public Map<String, Object> getAttributes() {
		return this.delegate.getAttributes();
	}

	public Principal getPrincipal() {
		return this.delegate.getPrincipal();
	}

	public InetSocketAddress getLocalAddress() {
		return this.delegate.getLocalAddress();
	}

	public InetSocketAddress getRemoteAddress() {
		return this.delegate.getRemoteAddress();
	}

	public String getAcceptedProtocol() {
		return this.delegate.getAcceptedProtocol();
	}

	public List<WebSocketExtension> getExtensions() {
		return this.delegate.getExtensions();
	}

	public boolean isOpen() {
		return this.delegate.isOpen();
	}

	public void sendMessage(Message<?> message) throws IOException {
		this.delegate.sendMessage(message);
	}

	public void close() throws IOException {
		this.delegate.close();
	}

	public void close(CloseStatus status) throws IOException {
		this.delegate.close(status);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [delegate=" + this.delegate + "]";
	}
}
