package io.basc.framework.security.session;

import java.util.stream.Stream;

import io.basc.framework.util.collections.Streams;

public interface UserSessions<T> extends Iterable<Session> {
	T getUid();

	int size();

	default Session getSession(String sessionId) {
		for (Session session : this) {
			if (sessionId.equals(session.getId())) {
				return session;
			}
		}
		return null;
	}

	default Stream<Session> stream() {
		return Streams.stream(iterator());
	}
}