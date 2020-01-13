package scw.rpc;

import java.lang.reflect.Method;

public abstract class RequestMessage {
	public abstract Class<?> getSourceClass();

	public abstract Method getMethod();

	public abstract Object[] getParameters();
	
	@Override
	public String toString() {
		Method method = getMethod();
		if (method == null) {
			return getSourceClass() + "[not found method]";
		}
		return method.toString();
	}
}
