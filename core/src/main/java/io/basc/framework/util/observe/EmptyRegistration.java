package io.basc.framework.util.observe;

public class EmptyRegistration implements Registration {

	@Override
	public boolean isCancellable() {
		return false;
	}

	@Override
	public boolean cancel() {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return true;
	}

}
