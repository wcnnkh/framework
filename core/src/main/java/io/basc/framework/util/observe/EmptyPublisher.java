package io.basc.framework.util.observe;

public class EmptyPublisher<T> implements Publisher<T> {

	@Override
	public Listenable<? extends Receipt> publish(T resource) {
		// TODO Auto-generated method stub
		return null;
	}

}
