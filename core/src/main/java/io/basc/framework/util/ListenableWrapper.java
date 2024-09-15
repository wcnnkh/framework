package io.basc.framework.util;

public interface ListenableWrapper<T extends Receipt, W extends Listenable<T>>
		extends Listenable<T>, RegistrationWrapper<W> {

	@Override
	default Registration registerListener(Listener<? super T> listener) {
		return getSource().registerListener(listener);
	}

	@Override
	default Listenable<T> onComplete(Listener<? super T> listener) {
		return getSource().onComplete(listener);
	}

	@Override
	default Listenable<T> onFailure(Listener<? super T> listener) {
		return getSource().onFailure(listener);
	}

	@Override
	default Listenable<T> onSuccess(Listener<? super T> listener) {
		return getSource().onSuccess(listener);
	}
}
