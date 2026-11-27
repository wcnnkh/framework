package run.soeasy.framework.core.exchange;

import java.util.function.Function;

import lombok.NonNull;

class MappedListenableTrace<S, T, W extends ListenableTrace<S>> extends MappedTrace<S, T, W>
		implements ListenableTrace<T> {

	public MappedListenableTrace(W source, Function<? super S, ? extends T> mapper) {
		super(source, mapper);
	}

	@Override
	public ListenableTrace<T> listenable() {
		return this;
	}

	@Override
	public Signal onSuccess(Listener<Trace<T>> listener) {
		return getSource().onSuccess((e) -> e.map(getMapper()));
	}

	@Override
	public Signal onFailure(Listener<Trace<T>> listener) {
		return getSource().onFailure((e) -> e.map(getMapper()));
	}

	@Override
	public Signal registerListener(@NonNull Listener<Trace<T>> listener) {
		return getSource().registerListener((e) -> e.map(getMapper()));
	}

}
