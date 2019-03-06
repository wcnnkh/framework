package scw.beans.rpc.dubbo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import scw.transaction.tcc.StageType;
import scw.transaction.tcc.TCC;
import scw.transaction.tcc.TCCManager;

class TransactionProxy implements InvocationHandler {
	private final Object obj;
	private final Class<?> interfaceClass;

	public TransactionProxy(Class<?> interfaceClass, Object obj) {
		this.obj = obj;
		this.interfaceClass = interfaceClass;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object rtn = method.invoke(obj, args);
		TCC tcc = method.getAnnotation(TCC.class);
		if (tcc != null && tcc.stage() == StageType.Try) {
			TCCManager.transactionRollback(rtn, method, interfaceClass, tcc.name(), obj, args);
		}
		return rtn;
	}
}
