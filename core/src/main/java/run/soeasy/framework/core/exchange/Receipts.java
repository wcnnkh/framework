package run.soeasy.framework.core.exchange;

import java.util.List;

import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Elements;

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
		List<Throwable> list = getElements().filter((e) -> e.isDone() && !e.isSuccess()).map((e) -> e.cause()).toList();
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		Throwable throwable = new Throwable();
		for (Throwable e : list) {
			throwable.addSuppressed(e);
		}
		return throwable;
	}
}
