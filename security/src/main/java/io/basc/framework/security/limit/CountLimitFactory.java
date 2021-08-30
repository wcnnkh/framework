package io.basc.framework.security.limit;

import io.basc.framework.beans.annotation.AutoImpl;
import io.basc.framework.reflect.MethodInvoker;
import io.basc.framework.security.limit.annotation.CountLimitSecurity;

@AutoImpl({ DefaultCountLimitFactory.class })
public interface CountLimitFactory {
	String getKey(CountLimitSecurity countLimitSecurity, MethodInvoker invoker, Object[] args);
}