package shuchaowen.core.beans;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.beans.annotaion.Transaction;
import shuchaowen.core.db.TransactionContext;
import shuchaowen.core.http.server.annotation.Controller;

public class TransactionMethodInterceptor implements MethodInterceptor{
	public static boolean isTransaction(Class<?> type, Method method) {
		boolean isTransaction = false;
		Controller controller = type.getAnnotation(Controller.class);
		if (controller != null) {
			isTransaction = true;
		}

		Service service = type.getAnnotation(Service.class);
		if (service != null) {
			isTransaction = true;
		}

		Transaction transaction = type.getAnnotation(Transaction.class);
		if (transaction != null) {
			isTransaction = transaction.value();
		}

		Transaction transaction2 = method.getAnnotation(Transaction.class);
		if (transaction2 != null) {
			isTransaction = transaction2.value();
		}
		
		return isTransaction;
	}
	
	
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		boolean isTransaction = isTransaction(obj.getClass(), method);
		if (isTransaction) {
			TransactionContext.getInstance().begin();
			Object rtn;
			try {
				rtn = proxy.invokeSuper(obj, args);
				TransactionContext.getInstance().commit();
				return rtn;
			} catch (Throwable e) {
				throw e;
			} finally {
				TransactionContext.getInstance().end();
			}
		} else {
			return proxy.invokeSuper(obj, args);
		}
	}
}
