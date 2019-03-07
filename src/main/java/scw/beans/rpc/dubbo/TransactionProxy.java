package scw.beans.rpc.dubbo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import scw.beans.BeanFactory;
import scw.transaction.tcc.TCCManager;

class TransactionProxy implements InvocationHandler {
	private final Object obj;
	private final Class<?> interfaceClass;
	private final BeanFactory beanFactory;

	public TransactionProxy(BeanFactory beanFactory, Class<?> interfaceClass, Object obj) {
		this.beanFactory = beanFactory;
		this.obj = obj;
		this.interfaceClass = interfaceClass;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object rtn = method.invoke(obj, args);
		TCCManager.transaction(beanFactory, interfaceClass, rtn, obj, method, args);
		return rtn;
	}
}
