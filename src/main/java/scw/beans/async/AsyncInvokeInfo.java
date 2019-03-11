package scw.beans.async;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import scw.beans.BeanFactory;
import scw.beans.annotaion.AsyncComplete;
import scw.common.MethodConfig;

class AsyncInvokeInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private MethodConfig methodConfig;
	private long delayMillis;
	private TimeUnit timeUnit;
	private Object[] args;

	public AsyncInvokeInfo() {
	};

	public AsyncInvokeInfo(AsyncComplete asyncComplete, Class<?> clz, Method method, Object[] args) {
		this.delayMillis = asyncComplete.delayMillis();
		this.methodConfig = new MethodConfig(clz, method);
		this.timeUnit = asyncComplete.timeUnit();
		this.args = args;
	}

	public long getDelayMillis() {
		return delayMillis;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public Object invoke(BeanFactory beanFactory) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		Object bean = beanFactory.get(methodConfig.getClz());
		return methodConfig.getMethod().invoke(bean, args);
	}
}
