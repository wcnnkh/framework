package scw.mq;

public interface Producer<T> {
	void push(String name, T message);
}
