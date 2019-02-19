package scw.sql.transaction;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFilter;
import scw.beans.BeanFilterChain;

/**
 * 必须要在BeanFactory中管理
 * 
 * @author shuchaowen
 *
 */
public class TransactionBeanFilter implements BeanFilter {
	/**
	 * 默认的事务定义
	 */
	private final TransactionDefinition transactionDefinition;

	public TransactionBeanFilter() {
		this(new DefaultTransactionDefinition());
	}

	public TransactionBeanFilter(TransactionDefinition transactionDefinition) {
		this.transactionDefinition = transactionDefinition;
	}

	public Object doFilter(Object obj, Method method, Object[] args, MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		Transactional clzTx = method.getDeclaringClass().getDeclaringClass().getAnnotation(Transactional.class);
		Transactional methodTx = method.getAnnotation(Transactional.class);
		if (clzTx == null && methodTx == null) {
			if (TransactionManager.hasTransaction()) {
				return beanFilterChain.doFilter(obj, method, args, proxy);
			} else {
				return first(obj, method, args, proxy, beanFilterChain);
			}
		}

		MultipleConnectionTransactionSynchronization transaction = TransactionManager
				.getTransaction(new AnnoationTransactionDefinition(clzTx, methodTx));
		Object rtn;
		try {
			rtn = beanFilterChain.doFilter(obj, method, args, proxy);
			TransactionManager.process(transaction);
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
			throw e;
		}
		return rtn;
	}

	private Object first(Object obj, Method method, Object[] args, MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		MultipleConnectionTransactionSynchronization mcts = TransactionManager
				.getTransaction(transactionDefinition);
		Object v;
		try {
			v = beanFilterChain.doFilter(obj, method, args, proxy);
			TransactionManager.process(mcts);
			return v;
		} catch (Throwable e) {
			TransactionManager.rollback(mcts);
			throw e;
		}
	}

}
