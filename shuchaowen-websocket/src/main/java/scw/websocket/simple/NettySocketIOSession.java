package scw.websocket.simple;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.protocol.Packet;
import com.corundumstudio.socketio.protocol.PacketType;

public class NettySocketIOSession extends SimpleWebSocketSession {
	private SocketIOClient client;
	private String eventName;

	public NettySocketIOSession(SocketIOClient client, String eventName) {
		this.client = client;
	}

	public void sendText(String text) throws IOException {
		this.client.sendEvent(eventName, text);
	}

	public String getId() {
		return client.getSessionId().toString();
	}

	public boolean isOpen() {
		return client.isChannelOpen();
	}

	public void close() throws IOException {
		client.disconnect();
	}

	public void sendBinary(ByteBuffer byteBuffer) throws IOException {
		client.sendEvent(eventName, byteBuffer);
	}

	public void sendPing(ByteBuffer byteBuffer) throws IOException {
		Packet packet = new Packet(PacketType.PING);
		packet.setSubType(PacketType.EVENT);
		packet.setName(eventName);
		packet.setData(byteBuffer);
		client.send(packet);
	}

	public void sendPong(ByteBuffer byteBuffer) throws IOException {
		Packet packet = new Packet(PacketType.PONG);
		packet.setSubType(PacketType.EVENT);
		packet.setName(eventName);
		packet.setData(byteBuffer);
		client.send(packet);
	}

	public void sendObject(Object data) throws IOException {
		client.sendEvent(eventName, data);
	}

}
