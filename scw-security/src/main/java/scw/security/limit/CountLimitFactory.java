package scw.security.limit;

import scw.aop.MethodInvoker;
import scw.beans.annotation.AutoImpl;
import scw.security.limit.annotation.CountLimitSecurity;

@AutoImpl({ DefaultCountLimitFactory.class })
public interface CountLimitFactory {
	String getKey(CountLimitSecurity countLimitSecurity, MethodInvoker invoker, Object[] args);
}