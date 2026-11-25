package run.soeasy.framework.core.exchange;

import java.util.function.Function;

import lombok.NonNull;

class MappedListenableStage<S, T, W extends ListenableStage<S>> extends MappedStage<S, T, W>
		implements ListenableStage<T> {

	public MappedListenableStage(W source, Function<? super S, ? extends T> mapper) {
		super(source, mapper);
	}

	@Override
	public ListenableStage<T> listenable() {
		return this;
	}

	@Override
	public Signal registerSuccessListener(Listener<Stage<T>> listener) {
		return getSource().registerSuccessListener((e) -> e.map(getMapper()));
	}

	@Override
	public Signal registerFailureListener(Listener<Stage<T>> listener) {
		return getSource().registerFailureListener((e) -> e.map(getMapper()));
	}

	@Override
	public Signal registerListener(@NonNull Listener<Stage<T>> listener) {
		return getSource().registerListener((e) -> e.map(getMapper()));
	}

}
