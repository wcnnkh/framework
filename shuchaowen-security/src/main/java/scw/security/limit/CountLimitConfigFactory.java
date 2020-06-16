package scw.security.limit;

import java.lang.reflect.Method;

import scw.beans.annotation.AopEnable;
import scw.beans.annotation.AutoImpl;

@AutoImpl({DefaultCountLimitConfigFactory.class})
@AopEnable(false)
public interface CountLimitConfigFactory {
	CountLimitConfig getCountLimitConfig(Class<?> clazz, Method method, Object[] args) throws Throwable;
}
