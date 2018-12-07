package shuchaowen.mq;

import shuchaowen.common.exception.ShuChaoWenRuntimeException;

public final class ConsumerRunnable<T> implements Runnable{
	private final Consumer<T> consumer;
	private final T message;
	
	public ConsumerRunnable(Consumer<T> consumer, T message){
		this.consumer = consumer;
		this.message = message;
	}
	public void run() {
		try {
			consumer.handler(message);
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
}
