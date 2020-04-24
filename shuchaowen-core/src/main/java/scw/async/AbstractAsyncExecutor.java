package scw.async;

import java.util.LinkedList;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.transaction.DefaultTransactionDefinition;
import scw.transaction.Transaction;
import scw.transaction.TransactionManager;

public abstract class AbstractAsyncExecutor implements AsyncExecutor {
	protected final LinkedList<AsyncLifeCycle> lifeCycles = new LinkedList<AsyncLifeCycle>();
	private final BeanFactory beanFactory;

	public AbstractAsyncExecutor(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void addAsyncLifeCycle(AsyncLifeCycle asyncLifeCycle) {
		lifeCycles.add(asyncLifeCycle);
	}

	public abstract void execute(AsyncRunnable asyncRunnable);

	protected Object call(AsyncRunnable asyncRunnable) throws Throwable {
		Transaction transaction = TransactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			for (AsyncLifeCycle lifeCycle : lifeCycles) {
				lifeCycle.executeAfter(asyncRunnable);
			}

			if (beanFactory != null) {
				Class<?> clazz = beanFactory.getAop().getUserClass(asyncRunnable.getClass());
				BeanDefinition definition = beanFactory.getDefinition(clazz);
				if (definition != null) {
					definition.init(asyncRunnable);
				}
			}

			Object v = asyncRunnable.call();
			for (AsyncLifeCycle lifeCycle : lifeCycles) {
				lifeCycle.executeBefore(asyncRunnable);
			}
			TransactionManager.commit(transaction);
			return v;
		} catch (Throwable e) {
			try {
				for (AsyncLifeCycle lifeCycle : lifeCycles) {
					lifeCycle.executeError(e, asyncRunnable);
				}
			} finally{
				TransactionManager.rollback(transaction);
			}
			throw e;
		} finally {
			for (AsyncLifeCycle lifeCycle : lifeCycles) {
				lifeCycle.executeComplete(asyncRunnable);
			}
		}
	}
}
