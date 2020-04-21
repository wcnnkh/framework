package scw.transaction.tcc;

import java.lang.reflect.Method;

import scw.async.AbstractAsyncRunnable;
import scw.beans.BeanFactory;
import scw.core.reflect.MethodHolder;
import scw.core.reflect.SerializableMethodHolder;

public abstract class TccMethod extends AbstractAsyncRunnable {
	private static final long serialVersionUID = 1L;
	private final MethodHolder methodHolder;

	public TccMethod(BeanFactory beanFactory, String beanId,
			Class<?> belongClass, Method method) {
		this.methodHolder = new SerializableMethodHolder(belongClass, method);
		setBeanFactory(beanFactory);
	}

	public MethodHolder getMethodHolder() {
		return methodHolder;
	}
}
