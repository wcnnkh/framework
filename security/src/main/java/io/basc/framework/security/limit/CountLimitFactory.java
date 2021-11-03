package io.basc.framework.security.limit;

import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.security.limit.annotation.CountLimitSecurity;

public interface CountLimitFactory {
	String getKey(CountLimitSecurity countLimitSecurity, MethodInvoker invoker, Object[] args);
}