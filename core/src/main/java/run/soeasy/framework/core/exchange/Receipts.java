package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.lang.Throwables;

@FunctionalInterface
public interface Receipts<R extends Receipt> extends Registrations<R>, Receipt {

	public static <E extends Receipt> Receipts<E> of(Elements<E> elements) {
		return () -> elements;
	}

	@SuppressWarnings("unchecked")
	public static <E extends Receipt> Receipts<E> forArray(E... receipts) {
		return of(Elements.forArray(receipts));
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
