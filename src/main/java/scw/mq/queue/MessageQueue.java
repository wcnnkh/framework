package scw.mq.queue;

import scw.mq.MQ;

public class MessageQueue<E> extends AbstractQueue<E> {
	private MQ<E> mq;
	private String name;

	public MessageQueue(MQ<E> mq, String name) {
		this.mq = mq;
		this.name = name;
		mq.bindConsumer(name, this);
	}

	public void push(E message) {
		mq.push(name, message);
	}
}
