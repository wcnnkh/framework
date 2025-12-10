package run.soeasy.framework.core.exchange;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;

@FunctionalInterface
public interface ListenableWrapper<T, W extends Listenable<T>> extends Listenable<T>, Wrapper<W> {
	@Override
	default BatchListenable<T> batch() {
		return getSource().batch();
	}

	@Override
	default Operation registerListener(@NonNull Listener<T> listener) {
		return getSource().registerListener(listener);
	}

}
