package scw.utils.mq.rabbit;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import scw.beans.annotaion.Destroy;
import scw.beans.annotaion.InitMethod;

public final class Rabbit extends ConnectionFactory {
	private Connection connection;
	private ExecutorService executorService;

	public Rabbit() {
		executorService = new ThreadPoolExecutor(1, 20, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public void submit(Runnable runnable) {
		executorService.submit(runnable);
	}

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

	@Destroy
	public void close() throws IOException {
		executorService.shutdownNow();
		connection.close();
	}
}
