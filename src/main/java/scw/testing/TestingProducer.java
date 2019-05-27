package scw.testing;

public interface TestingProducer<T> {
	void producer(T message);
}
