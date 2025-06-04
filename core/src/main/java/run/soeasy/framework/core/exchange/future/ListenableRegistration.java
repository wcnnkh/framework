package run.soeasy.framework.core.exchange.future;

import run.soeasy.framework.core.exchange.Listenable;
import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.RegistrationWrapper;

/**
 * 可监听的
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface ListenableRegistration<T extends Receipt> extends Listenable<T>, Registration {

	public static interface ListenableRegistrationWrapper<T extends Receipt, W extends ListenableRegistration<T>>
			extends ListenableRegistration<T>, RegistrationWrapper<W> {

		@Override
		default Registration registerListener(Listener<T> listener) {
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

	default ListenableRegistration<T> onComplete(Listener<? super T> listener) {
		registerListener((event) -> {
			if (event.isDone()) {
				listener.accept(event);
			}
		});
		return this;
	}

	default ListenableRegistration<T> onFailure(Listener<? super T> listener) {
		return onComplete((event) -> {
			if (!event.isSuccess()) {
				listener.accept(event);
			}
		});
	}

	default ListenableRegistration<T> onSuccess(Listener<? super T> listener) {
		return onComplete((event) -> {
			if (event.isSuccess()) {
				listener.accept(event);
			}
		});
	}
}
