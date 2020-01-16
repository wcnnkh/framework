package scw.rcp.object;

import java.lang.reflect.Method;

import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.rcp.Service;
import scw.serializer.JavaSerializer;
import scw.serializer.Serializer;

public abstract class ObjectHttpService implements Service {
	private Serializer serializer = JavaSerializer.SERIALIZER;

	public Serializer getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	public Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args,
			FilterChain filterChain) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}
}
