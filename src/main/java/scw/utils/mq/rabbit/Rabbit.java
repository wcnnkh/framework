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
import com.rabbitmq.client.Envelope;

import scw.beans.annotation.Destroy;
import scw.beans.annotation.InitMethod;
import scw.core.Constants;
import scw.core.serializer.NoTypeSpecifiedSerializer;
import scw.core.serializer.Serializer;

public final class Rabbit extends ConnectionFactory {
	private final NoTypeSpecifiedSerializer serializer;
	
	public Rabbit(){
		this(Constants.DEFAULT_SERIALIZER);
	}
	
	public Rabbit(Serializer serializer){
		this.serializer = serializer;
		executorService = new ThreadPoolExecutor(1, 20, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	private volatile Connection singleConnection;

	/**
	 * 获取一个单例的连接
	 * 
	 * @return
	 */
	public Connection getSingleConnection() {
		if (singleConnection == null) {
			synchronized (this) {
				if (singleConnection == null) {
					try {
						singleConnection = newConnection();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return singleConnection;
	}
	
	public NoTypeSpecifiedSerializer getSerializer() {
		return serializer;
	}

	private Connection connection;
	private ExecutorService executorService;

	public void submit(Runnable runnable) {
		executorService.submit(runnable);
	}

	/**
	 * ack确认
	 * @param channel
	 * @param envelope
	 * @param multiple
	 */
	public void basicAck(Channel channel, Envelope envelope, boolean multiple) {
		try {
			channel.basicAck(envelope.getDeliveryTag(), multiple);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
