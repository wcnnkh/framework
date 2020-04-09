package scw.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;

import scw.lang.UnsupportedException;
import scw.util.message.BinaryMessage;
import scw.util.message.FragmentMessage;
import scw.util.message.Message;
import scw.util.message.TextMessage;

public abstract class AbstractWebSocketSession implements WebSocketSession {

	public void send(Message<?> message) throws Exception {
		if (message instanceof TextMessage) {
			if (message instanceof FragmentMessage) {
				sendText(((TextMessage) message).getPayload(),
						((FragmentMessage<?>) message).isLast());
			} else {
				sendText(((TextMessage) message).getPayload());
			}
		}
		if (message instanceof BinaryMessage) {
			if (message instanceof FragmentMessage) {
				sendBinary(((BinaryMessage) message).getPayload(),
						((FragmentMessage<?>) message).isLast());
			} else {
				sendBinary(((BinaryMessage) message).getPayload());
			}
		} else if (message instanceof PingMessage) {
			sendPing(((PingMessage) message).getPayload());
		} else if (message instanceof PongMessage) {
			sendPong(((PongMessage) message).getPayload());
		} else {
			if (message instanceof FragmentMessage) {
				sendObject(message.getPayload(),
						((FragmentMessage<?>) message).isLast());
			} else {
				sendObject(message.getPayload());
			}
		}
	}

	public void sendText(String fragment, boolean last) throws IOException {
		throw new UnsupportedException(fragment);
	}

	public void sendBinary(ByteBuffer fragment, boolean last) throws Exception {
		throw new UnsupportedException(fragment.toString());
	}
	
	public void sendObject(Object data) throws Exception {
		if (data instanceof String) {
			sendText((String) data);
		} else if (data instanceof ByteBuffer) {
			sendBinary((ByteBuffer) data);
		} else {
			throw new UnsupportedException(data.toString());
		}
	}

	public void sendObject(Object fragment, boolean last) throws Exception {
		if (fragment instanceof String) {
			sendText((String) fragment, last);
		} else if (fragment instanceof ByteBuffer) {
			sendBinary((ByteBuffer) fragment, last);
		} else {
			throw new UnsupportedException(fragment.toString());
		}
	}
}
