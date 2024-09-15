package io.basc.framework.util;

public class Cancelled implements Registration {

	@Override
	public boolean cancel() {
		return false;
	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return true;
	}

}
