package run.soeasy.framework.core.exchange;

import lombok.NonNull;

class IgnoreListenable<T> implements Listenable<T> {
	static final IgnoreListenable<?> INSTANCE = new IgnoreListenable<>();
	private static final Signal UNSUPPORTED = Signal.failure(new UnsupportedOperationException("Listenable"));

	@Override
	public Signal registerListener(@NonNull Listener<T> listener) {
		return UNSUPPORTED;
	}

}
