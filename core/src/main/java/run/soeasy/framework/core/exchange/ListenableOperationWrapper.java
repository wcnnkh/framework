package run.soeasy.framework.core.exchange;

@FunctionalInterface
public interface ListenableOperationWrapper<T extends Operation, W extends ListenableOperation<T>>
		extends ListenableOperation<T>, OperationWrapper<W>, ListenableWrapper<T, W> {
	@Override
	default ListenableOperation<T> onComplete(Listener<? super T> listener) {
		return getSource().onComplete(listener);
	}

	@Override
	default ListenableOperation<T> onFailure(Listener<? super T> listener) {
		return getSource().onFailure(listener);
	}

	@Override
	default ListenableOperation<T> onSuccess(Listener<? super T> listener) {
		return getSource().onSuccess(listener);
	}
}
