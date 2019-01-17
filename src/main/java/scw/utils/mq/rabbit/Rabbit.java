package scw.utils.mq.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import scw.beans.annotaion.InitMethod;

public final class Rabbit extends ConnectionFactory {
	private Connection connection;

	@InitMethod
	private void init() throws IOException, TimeoutException {
		connection = newConnection();
	}

	public Channel createChannel() throws IOException {
		return connection.createChannel();
	}

	public Channel createChannel(int channelNumber) throws IOException {
		return connection.createChannel(channelNumber);
	}

	public Connection getConnection() {
		return connection;
	}
}
