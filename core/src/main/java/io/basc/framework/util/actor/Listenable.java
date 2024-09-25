package io.basc.framework.util.actor;

import io.basc.framework.util.Listener;
import io.basc.framework.util.Observable;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registration;

/**
 * 可监听的
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Listenable<T extends Receipt> extends Observable<T>, Registration {
	default Listenable<T> onComplete(Listener<? super T> listener) {
		registerListener((event) -> {
			if (event.isDone()) {
				listener.accept(event);
			}
		});
		return this;
	}

	default Listenable<T> onFailure(Listener<? super T> listener) {
		return onComplete((event) -> {
			if (!event.isSuccess()) {
				listener.accept(event);
			}
		});
	}

	default Listenable<T> onSuccess(Listener<? super T> listener) {
		return onComplete((event) -> {
			if (event.isSuccess()) {
				listener.accept(event);
			}
		});
	}
}
