package io.basc.framework.mvc.security;

import io.basc.framework.security.session.UserSessionFactory;

public interface UserSessionFactoryAdapter {
	<T> UserSessionFactory<T> getUserSessionFactory(Class<T> type);
}
