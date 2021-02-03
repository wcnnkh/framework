package scw.aop.support;

import java.io.Serializable;

import scw.aop.MethodInterceptor;
import scw.core.reflect.MethodInvoker;

public abstract class AbstractMethodInterceptors implements Iterable<MethodInterceptor>, Serializable, MethodInterceptor {
	private static final long serialVersionUID = 1L;
	private Object instance;

	public Object getInstance() {
		return instance;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}

	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		MethodInvoker wrapper;
		if(instance == null){
			wrapper = new MethodInterceptorsInvoker(invoker, iterator());
		}else{
			wrapper = new InstanceMethodInterceptorsInvoker(invoker, iterator(), instance);
		}
		return wrapper.invoke(args);
	}
}

