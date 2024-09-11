package io.basc.framework.util.observe;

import io.basc.framework.util.observe.future.ListenableReceipt;

public class EmptyPublisher<T> implements Publisher<T> {

	@Override
	public ListenableReceipt<?> publish(T resource) {
		// TODO Auto-generated method stub
		return null;
	}

}
