package io.basc.framework.transaction.aop;

import io.basc.framework.execution.Function;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.transaction.RollbackOnly;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionDefinition;
import io.basc.framework.transaction.TransactionManager;
import io.basc.framework.transaction.TransactionUtils;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;

/**
 * 以aop的方式管理事务
 * 
 * @author wcnnkh
 *
 */
public final class TransactionExecutionInterceptor extends DefaultTransactionDefinitionLoader
		implements ExecutionInterceptor {
	private static Logger logger = LoggerFactory.getLogger(TransactionExecutionInterceptor.class);

	private void invokerAfter(Transaction transaction, Object rtn, Function function) {
		if (rtn != null && (rtn instanceof RollbackOnly)) {
			RollbackOnly result = (RollbackOnly) rtn;
			if (result.isRollbackOnly()) {
				transaction.setRollbackOnly();
				if (logger.isDebugEnabled()) {
					logger.debug("rollback only in {}", function);
				}
			}
		}
	}

	@Override
	public Object intercept(Function function, Elements<? extends Object> args) throws Throwable {
		TransactionManager transactionManager = TransactionUtils.getManager();
		TransactionDefinition transactionDefinition = load(function);
		if (transactionDefinition == null) {
			Object rtn = function.execute(args);
			if (transactionManager.hasTransaction()) {
				invokerAfter(transactionManager.getTransaction(), rtn, function);
			}
			return rtn;
		}

		Transaction transaction = transactionManager.getTransaction(transactionDefinition);
		Object v;
		try {
			v = function.execute(args);
			invokerAfter(transaction, v, function);
			transactionManager.commit(transaction);
			return v;
		} catch (Throwable e) {
			if (isRollback(function, e)) {
				transactionManager.rollback(transaction);
			} else {
				// 重复的commit会直接调用关闭
				transactionManager.commit(transaction);
			}
			throw e;
		}
	}
}
