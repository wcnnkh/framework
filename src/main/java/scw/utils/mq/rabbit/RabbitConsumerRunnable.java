package scw.utils.mq.rabbit;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

/**
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public abstract class RabbitConsumerRunnable<T> implements Runnable {
	private final T message;
	private final Channel channel;
	private final String consumerTag;
	private final Envelope envelope;

	public RabbitConsumerRunnable(Channel channel, String consumerTag, Envelope envelope, T message) {
		this.message = message;
		this.channel = channel;
		this.consumerTag = consumerTag;
		this.envelope = envelope;
	}

	protected abstract void begin() throws Throwable;

	protected abstract void process() throws Throwable;

	protected abstract void end();

	protected void ack() {
		try {
			getChannel().basicAck(envelope.getDeliveryTag(), false);
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

	public String getConsumerTag() {
		return consumerTag;
	}

	public Envelope getEnvelope() {
		return envelope;
	}
}
