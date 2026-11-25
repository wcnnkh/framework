package run.soeasy.framework.core.exchange;

import java.util.function.Function;

import lombok.NonNull;

public interface ListenableStage<V> extends Stage<V>, ListenableSignal<Stage<V>> {
	@Override
	default ListenableStage<V> listenable() {
		return this;
	}

	@Override
	default <R> ListenableStage<R> map(@NonNull Function<? super V, ? extends R> mapper) {
		return new MappedListenableStage<>(this, mapper);
	}
}
