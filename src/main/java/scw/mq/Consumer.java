package scw.mq;

public interface Consumer<T> {
	void consumer(T message) throws Throwable;
}
