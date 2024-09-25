package io.basc.framework.util;

import java.io.Serializable;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Registed implements Registration, Serializable {
	private static final long serialVersionUID = 1L;
	private final boolean cancelled;

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
		return cancelled;
	}
}
