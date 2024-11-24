package io.basc.framework.security.limit;

import io.basc.framework.security.limit.annotation.CountLimitSecurity;
import io.basc.framework.util.reflect.MethodInvoker;

public interface CountLimitFactory {
	String getKey(CountLimitSecurity countLimitSecurity, MethodInvoker invoker, Object[] args);
}