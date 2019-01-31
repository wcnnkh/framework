package scw.transaction;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFactory;
import scw.beans.BeanFilter;
import scw.beans.BeanFilterChain;
import scw.common.utils.StringUtils;

public class TransactionBeanFilter implements BeanFilter {
	private BeanFactory beanFactory;

	public TransactionBeanFilter(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public Object doFilter(Object obj, Method method, Object[] args, MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		Transactional clzTx = method.getDeclaringClass().getDeclaringClass().getAnnotation(Transactional.class);
		Transactional methodTx = method.getAnnotation(Transactional.class);
		if (clzTx == null && methodTx == null) {
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}

		String transactionManagerBeanName;
		if (clzTx == null) {
			transactionManagerBeanName = (methodTx == null ? null : methodTx.transactionManager());
		} else {
			transactionManagerBeanName = methodTx == null ? clzTx.transactionManager() : methodTx.transactionManager();
		}

		if (StringUtils.isEmpty(transactionManagerBeanName)) {
			transactionManagerBeanName = TransactionManager.class.getName();
		}

		TransactionManager transactionManager = beanFactory.get(transactionManagerBeanName);
		Transaction transaction = null;
		Object rtn;
		try {
			transaction = transactionManager.getTransaction(new AnnoationTransactionDefinition(clzTx, methodTx));
			rtn = beanFilterChain.doFilter(obj, method, args, proxy);
			transactionManager.commit(transaction);
		} catch (Throwable e) {
			transactionManager.rollback(transaction);
			throw e;
		}
		return rtn;
	}

}
