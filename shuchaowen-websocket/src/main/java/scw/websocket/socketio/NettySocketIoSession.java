package scw.websocket.socketio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.Map.Entry;

import com.corundumstudio.socketio.SocketIOClient;

import scw.messageing.BinaryMessage;
import scw.messageing.TextMessage;
import scw.net.http.HttpHeaders;
import scw.websocket.CloseStatus;
import scw.websocket.PingMessage;
import scw.websocket.PongMessage;
import scw.websocket.WebSocketExtension;
import scw.websocket.adapter.AbstractWebSocketSession;

public class NettySocketIoSession extends AbstractWebSocketSession<SocketIOClient>{
	private HttpHeaders httpHeaders;
	
	public NettySocketIoSession(SocketIOClient socketIOClient, String eventName) {
		super(null);
	}

	public String getId() {
		checkNativeSessionInitialized();
		return getNativeSession().getSessionId().toString();
	}
	
	@Override
	public void initializeNativeSession(SocketIOClient session) {
		super.initializeNativeSession(session);
		this.httpHeaders = new HttpHeaders();
		for(Entry<String, String> entry : session.getHandshakeData().getHttpHeaders()){
			httpHeaders.add(entry.getKey(), entry.getValue());
		}
		this.httpHeaders.readyOnly();
	}

	public URI getUri() {
		String url = getNativeSession().getHandshakeData().getUrl();
		try {
			return new URI(url);
		} catch (URISyntaxException e) {
			logger.error(e, url);
		}
		return null;
	}

	public HttpHeaders getHandshakeHeaders() {
		return httpHeaders;
	}

	public Principal getPrincipal() {
		return null;
	}

	public InetSocketAddress getLocalAddress() {
		return getNativeSession().getHandshakeData().getLocal();
	}

	public InetSocketAddress getRemoteAddress() {
		return (InetSocketAddress) getNativeSession().getRemoteAddress();
	}

	public String getAcceptedProtocol() {
		return null;
	}

	public void setTextMessageSizeLimit(int messageSizeLimit) {
		
	}

	public int getTextMessageSizeLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setBinaryMessageSizeLimit(int messageSizeLimit) {
		// TODO Auto-generated method stub
		
	}

	public int getBinaryMessageSizeLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<WebSocketExtension> getExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void sendTextMessage(TextMessage message) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void sendBinaryMessage(BinaryMessage message) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void sendPingMessage(PingMessage message) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void sendPongMessage(PongMessage message) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void closeInternal(CloseStatus status) throws IOException {
	}

}
