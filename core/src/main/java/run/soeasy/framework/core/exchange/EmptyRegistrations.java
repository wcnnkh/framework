package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;

class EmptyRegistrations<R extends Registration> implements Registrations<R> {
	static final EmptyRegistrations<?> INSTANCE = new EmptyRegistrations<>();

	@Override
	public Elements<R> getElements() {
		return Elements.empty();
	}
}