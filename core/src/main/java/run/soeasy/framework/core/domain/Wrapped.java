package run.soeasy.framework.core.domain;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.ObjectUtils;

@RequiredArgsConstructor
public class Wrapped<T> implements Wrapper<T> {
	@NonNull
	protected final T source;

	@Override
	public T getSource() {
		return source;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(source);
	}

	@Override
	public String toString() {
		return ObjectUtils.toString(source);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Wrapped) {
			return ObjectUtils.equals(this.source, ((Wrapped<?>) obj).source);
		}
		return ObjectUtils.equals(this.source, obj);
	}
}
