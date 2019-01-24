package scw.utils.mq.rabbit;

import java.io.IOException;

import com.rabbitmq.client.Channel;

/**
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public abstract class RabbitConsumerRunnable<T> implements Runnable {
	private final T message;
	private final Channel channel;
	private final long deliveryTag;

	public RabbitConsumerRunnable(T message, Channel channel, long deliveryTag) {
		this.message = message;
		this.channel = channel;
		this.deliveryTag = deliveryTag;
	}

	protected abstract void begin() throws InterruptedException;

	protected abstract void process() throws Throwable;

	protected abstract void end();

	protected void ack() {
		try {
			getChannel().basicAck(deliveryTag, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final void run() {
		try {
			begin();
			process();
			end();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public T getMessage() {
		return message;
	}

	public Channel getChannel() {
		return channel;
	}

	public long getDeliveryTag() {
		return deliveryTag;
	}
}
