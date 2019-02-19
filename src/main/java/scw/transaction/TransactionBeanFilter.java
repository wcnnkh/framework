package scw.transaction;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFactory;
import scw.beans.BeanFilter;
import scw.beans.BeanFilterChain;
import scw.beans.annotaion.Autowrite;
import scw.common.utils.StringUtils;
import scw.transaction.def.DefaultTransactionManager;
import scw.transaction.def.DefaultTransactionUtils;
import scw.transaction.def.MultipleConnectionTransactionSynchronization;

/**
 * 必须要在BeanFactory中管理
 * 
 * @author shuchaowen
 *
 */
public class TransactionBeanFilter implements BeanFilter {
	@Autowrite
	private BeanFactory beanFactory;
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

	public Object doFilter(Object obj, Method method, Object[] args,
			MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		Transactional clzTx = method.getDeclaringClass().getAnnotation(
				Transactional.class);
		Transactional methodTx = method.getAnnotation(Transactional.class);
		if (clzTx == null && methodTx == null) {
			if (DefaultTransactionUtils.hasTransaction()) {
				return beanFilterChain.doFilter(obj, method, args, proxy);
			} else {
				return first(obj, method, args, proxy, beanFilterChain);
			}
		}

		String tmName = null;
		if (clzTx != null) {
			tmName = clzTx.transactionManager();
		}

		if (methodTx != null) {
			if (!StringUtils.isEmpty(methodTx.transactionManager())) {
				tmName = methodTx.transactionManager();
			}
		}

		if (StringUtils.isEmpty(tmName)) {
			tmName = DefaultTransactionManager.class.getName();
		}

		TransactionManager transactionManager = beanFactory.get(tmName);
		Transaction transaction = transactionManager
				.getTransaction(new AnnoationTransactionDefinition(clzTx,
						methodTx));
		Object rtn;
		try {
			rtn = beanFilterChain.doFilter(obj, method, args, proxy);
			transactionManager.commit(transaction);
		} catch (Throwable e) {
			transactionManager.rollback(transaction);
			throw e;
		}
		return rtn;
	}

	private Object first(Object obj, Method method, Object[] args,
			MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		MultipleConnectionTransactionSynchronization mcts = DefaultTransactionUtils
				.getTransaction(transactionDefinition);
		Object v;
		try {
			v = beanFilterChain.doFilter(obj, method, args, proxy);
			DefaultTransactionUtils.commit(mcts);
			return v;
		} catch (Throwable e) {
			DefaultTransactionUtils.rollback(mcts);
			throw e;
		}
	}

}
