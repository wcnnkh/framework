package scw.websocket.simple;

import java.io.IOException;
import java.nio.ByteBuffer;

import scw.lang.NotSupportedException;
import scw.messageing.BinaryMessage;
import scw.messageing.FragmentMessage;
import scw.messageing.Message;
import scw.messageing.TextMessage;
import scw.messageing.session.Session;
import scw.websocket.PingMessage;
import scw.websocket.PongMessage;

public abstract class SimpleWebSocketSession implements Session {

	public void sendMessage(Message<?> message) throws IOException {
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
		throw new NotSupportedException(fragment);
	}

	public void sendBinary(ByteBuffer fragment, boolean last) throws IOException {
		throw new NotSupportedException(fragment.toString());
	}
	
	public void sendObject(Object data) throws IOException {
		if (data instanceof String) {
			sendText((String) data);
		} else if (data instanceof ByteBuffer) {
			sendBinary((ByteBuffer) data);
		} else {
			throw new NotSupportedException(data.toString());
		}
	}

	public void sendObject(Object fragment, boolean last) throws IOException {
		if (fragment instanceof String) {
			sendText((String) fragment, last);
		} else if (fragment instanceof ByteBuffer) {
			sendBinary((ByteBuffer) fragment, last);
		} else {
			throw new NotSupportedException(fragment.toString());
		}
	}
	
	public abstract void sendText(String text) throws IOException;

	public abstract void sendBinary(ByteBuffer byteBuffer) throws IOException;

	public abstract void sendPing(ByteBuffer byteBuffer) throws IOException;

	public abstract void sendPong(ByteBuffer byteBuffer) throws IOException;
}
