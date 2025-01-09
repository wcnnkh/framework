package io.basc.framework.util.exchange;

import io.basc.framework.util.Throwables;
import io.basc.framework.util.collection.Elements;

@FunctionalInterface
public interface Receipts<R extends Receipt> extends Registrations<R>, Receipt {

	public static <E extends Receipt> Receipts<E> of(Elements<E> elements) {
		return () -> elements;
	}

	@Override
	default boolean isDone() {
		return getElements().allMatch((e) -> e.isDone());
	}

	@Override
	default boolean isSuccess() {
		return getElements().allMatch((e) -> e.isSuccess());
	}

	@Override
	default Throwable cause() {
		Elements<Throwable> throwables = getElements().filter((e) -> e.isDone() && !e.isSuccess())
				.map((e) -> e.cause());
		if (throwables.isEmpty()) {
			return null;
		}
		return new Throwables(throwables);
	}
}
