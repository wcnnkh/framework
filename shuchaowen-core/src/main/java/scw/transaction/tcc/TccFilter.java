package scw.transaction.tcc;

import scw.aop.Filter;
import scw.aop.ProxyInvoker;
import scw.complete.Complete;
import scw.core.instance.NoArgsInstanceFactory;
import scw.core.instance.annotation.Configuration;
import scw.lang.NotSupportedException;
import scw.transaction.TransactionLifeCycle;
import scw.transaction.TransactionManager;
import scw.transaction.tcc.annotation.Tcc;

@Configuration(order = Integer.MAX_VALUE)
public class TccFilter implements Filter {
	private NoArgsInstanceFactory instanceFactory;

	public TccFilter(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public Object doFilter(ProxyInvoker invoker, Object[] args) throws Throwable {
		final Tcc tcc = invoker.getMethod().getAnnotation(Tcc.class);
		if (tcc == null) {
			return invoker.invoke(args);
		}

		if (!instanceFactory.isInstance(tcc.service())) {
			throw new NotSupportedException("not support tcc: " + invoker.getMethod().toString());
		}

		if (!TransactionManager.hasTransaction()) {
			throw new NotSupportedException("not exist transaction");
		}

		Object result = invoker.invoke(args);
		final TccService tccService = instanceFactory.getInstance(tcc.service());
		final Stage confirm = tccService.createConfirm(invoker, args, result, tcc);
		final Stage cancel = tccService.createCancel(invoker, args, result, tcc);
		if (confirm == null && cancel == null) {
			throw new TccException("confirm or cancel At least one: " + invoker.getMethod().toString());
		}

		if ((confirm != null && !confirm.isActive()) || (cancel != null && !cancel.isActive())) {
			throw new TccException("tcc definition error:" + invoker.getMethod());
		}

		// 先注册一个取消任务，以防止最坏的情况发生，那样还可以回滚
		final Complete cancelComplete = cancel == null ? null : tccService.registerComplete(cancel);
		TransactionManager.transactionLifeCycle(new TransactionLifeCycle() {

			public void complete() {
			}

			public void beforeRollback() {
			}

			public void beforeProcess() throws Throwable {
			}

			public void afterRollback() {
				if (cancelComplete != null) {
					cancelComplete.run();
				}
			}

			public void afterProcess() throws Throwable {
				if (cancelComplete != null) {
					cancelComplete.cancel();
				}

				if (confirm != null) {
					tccService.registerComplete(confirm).run();
				}
			}
		});
		return result;
	}

}
