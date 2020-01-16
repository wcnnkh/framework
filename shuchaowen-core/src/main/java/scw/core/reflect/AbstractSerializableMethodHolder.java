package scw.core.reflect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;

public abstract class AbstractSerializableMethodHolder extends AbstractMethodHolder implements Serializable {
	private static final long serialVersionUID = 1L;
	private String methodName;

	public AbstractSerializableMethodHolder(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodName() {
		return methodName;
	}

	private transient Method method;

	public Method getMethod() {
		if (this.method == null) {
			this.method = getMethodInternal();
		}
		return this.method;
	}

	private Method getMethodInternal() {
		return ReflectionUtils.findMethod(getBelongClass(), getMethodName(), getParameterTypes());
	}

	// 在进行反序列化时使用反射获取方法
	protected void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
		this.method = getMethodInternal();
	}
}
