package scw.transaction;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFactory;
import scw.beans.BeanFilter;
import scw.beans.BeanFilterChain;
import scw.beans.annotaion.Autowrite;
import scw.common.utils.StringUtils;
import scw.transaction.sql.MultipleConnectionTransactionManager;
import scw.transaction.sql.MultipleConnectionTransactionSynchronization;
import scw.transaction.sql.MultipleConnectionTransactionUtils;

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
	private final String defaltTransactionManager;

	public TransactionBeanFilter() {
		this(new DefaultTransactionDefinition(), MultipleConnectionTransactionManager.class.getName());
	}

	public TransactionBeanFilter(TransactionDefinition transactionDefinition) {
		this(transactionDefinition, MultipleConnectionTransactionManager.class.getName());
	}

	public TransactionBeanFilter(String defaultTransactionManager) {
		this(new DefaultTransactionDefinition(), defaultTransactionManager);
	}

	public TransactionBeanFilter(TransactionDefinition transactionDefinition, String defaultTransactionManager) {
		this.transactionDefinition = transactionDefinition;
		this.defaltTransactionManager = defaultTransactionManager;
	}

	public Object doFilter(Object obj, Method method, Object[] args, MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		Transactional clzTx = method.getDeclaringClass().getDeclaringClass().getAnnotation(Transactional.class);
		Transactional methodTx = method.getAnnotation(Transactional.class);
		if (clzTx == null && methodTx == null) {
			if (MultipleConnectionTransactionUtils.hasTransaction()) {
				return beanFilterChain.doFilter(obj, method, args, proxy);
			} else {
				return first(obj, method, args, proxy, beanFilterChain);
			}
		}

		String transactionManagerBeanName;
		if (clzTx == null) {
			transactionManagerBeanName = (methodTx == null ? null : methodTx.transactionManager());
		} else {
			transactionManagerBeanName = methodTx == null ? clzTx.transactionManager() : methodTx.transactionManager();
		}

		if (StringUtils.isEmpty(transactionManagerBeanName)) {
			transactionManagerBeanName = defaltTransactionManager;
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

	private Object first(Object obj, Method method, Object[] args, MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		MultipleConnectionTransactionSynchronization mcts = MultipleConnectionTransactionUtils
				.getTransaction(transactionDefinition);
		Object v;
		try {
			v = beanFilterChain.doFilter(obj, method, args, proxy);
			MultipleConnectionTransactionUtils.process(mcts);
			return v;
		} catch (Throwable e) {
			MultipleConnectionTransactionUtils.rollback(mcts);
			throw e;
		}
	}

}
