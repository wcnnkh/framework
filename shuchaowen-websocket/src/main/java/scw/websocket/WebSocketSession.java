package scw.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;

import scw.util.message.session.Session;

public interface WebSocketSession extends Session {

	void sendText(String text) throws IOException;

	void sendBinary(ByteBuffer byteBuffer) throws IOException;

	void sendPing(ByteBuffer byteBuffer) throws IOException;

	void sendPong(ByteBuffer byteBuffer) throws IOException;

	void sendObject(Object data) throws Exception;
}
