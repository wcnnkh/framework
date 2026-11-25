package run.soeasy.framework.core.exchange;

import lombok.NonNull;

class IgnoreListenable<T> implements Listenable<T> {
	static final IgnoreListenable<?> INSTANCE = new IgnoreListenable<>();

	@Override
	public Signal registerListener(@NonNull Listener<T> listener) {
		return Signal.failure();
	}

}
