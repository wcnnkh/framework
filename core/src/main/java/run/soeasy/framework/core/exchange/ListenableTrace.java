package run.soeasy.framework.core.exchange;

import java.util.function.Function;

import lombok.NonNull;

public interface ListenableTrace<V> extends Trace<V>, Listenable<Trace<V>> {
	@Override
	default ListenableTrace<V> listenable() {
		return this;
	}

	@Override
	default <R> ListenableTrace<R> map(@NonNull Function<? super V, ? extends R> mapper) {
		return new MappedListenableTrace<>(this, mapper);
	}

	Signal registerSuccessListener(@NonNull Listener<Trace<V>> listener);

	Signal registerFailureListener(@NonNull Listener<Trace<V>> listener);
}