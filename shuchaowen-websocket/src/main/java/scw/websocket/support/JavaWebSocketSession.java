package scw.websocket.support;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.websocket.EncodeException;
import javax.websocket.Session;

import scw.lang.UnsupportedException;
import scw.websocket.AbstractWebSocketSession;

public class JavaWebSocketSession extends AbstractWebSocketSession{
	private Session session;
	
	public JavaWebSocketSession(Session session){
		this.session = session;
	}
	
	public void sendText(String text) throws IOException {
		this.session.getBasicRemote().sendText(text);
	}

	public void sendText(String fragment, boolean last) throws IOException,
			UnsupportedException {
		this.session.getBasicRemote().sendText(fragment, last);
	}

	public void sendBinary(ByteBuffer byteBuffer) throws IOException {
		this.session.getBasicRemote().sendBinary(byteBuffer);
	}

	public void sendBinary(ByteBuffer fragment, boolean last)
			throws IOException, UnsupportedException {
		this.session.getBasicRemote().sendBinary(fragment, last);
	}

	public void sendPing(ByteBuffer byteBuffer) throws IOException {
		this.session.getBasicRemote().sendPing(byteBuffer);
	}

	public void sendPong(ByteBuffer byteBuffer) throws IOException {
		this.session.getBasicRemote().sendPong(byteBuffer);
	}

	public void sendObject(Object data) throws IOException, EncodeException {
		this.session.getBasicRemote().sendObject(data);
	}

	public String getId() {
		return session.getId();
	}

	public boolean isOpen() {
		return session.isOpen();
	}

	public void close() throws IOException {
		session.close();
	}
}
