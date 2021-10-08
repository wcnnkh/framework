package io.basc.framework.security.session;

import java.util.ArrayList;
import java.util.stream.Stream;

public class DefaultUserSessions<T> extends ArrayList<Session> implements UserSessions<T> {
	private static final long serialVersionUID = 1L;
	private final T uid;

	public DefaultUserSessions(T uid) {
		this.uid = uid;
	}

	public T getUid() {
		return uid;
	}

	@Override
	public Stream<Session> stream() {
		return super.stream();
	}
}
