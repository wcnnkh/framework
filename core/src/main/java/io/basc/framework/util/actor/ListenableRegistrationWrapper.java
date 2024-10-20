package io.basc.framework.util.actor;

import io.basc.framework.util.Listener;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registration;
import io.basc.framework.util.RegistrationWrapper;

public interface ListenableRegistrationWrapper<T extends Receipt, W extends ListenableRegistration<T>>
		extends ListenableRegistration<T>, RegistrationWrapper<W> {

	@Override
	default Registration registerListener(Listener<? super T> listener) {
		return getSource().registerListener(listener);
	}

	@Override
	default ListenableRegistration<T> onComplete(Listener<? super T> listener) {
		return getSource().onComplete(listener);
	}

	@Override
	default ListenableRegistration<T> onFailure(Listener<? super T> listener) {
		return getSource().onFailure(listener);
	}

	@Override
	default ListenableRegistration<T> onSuccess(Listener<? super T> listener) {
		return getSource().onSuccess(listener);
	}
}
