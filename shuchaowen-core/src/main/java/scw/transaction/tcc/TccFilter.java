package scw.transaction.tcc;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.aop.ProxyContext;
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

	public Object doFilter(Invoker invoker, ProxyContext context, FilterChain filterChain) throws Throwable {
		final Tcc tcc = context.getMethod().getAnnotation(Tcc.class);
		if (tcc == null) {
			return filterChain.doFilter(invoker, context);
		}

		if (!instanceFactory.isInstance(tcc.service())) {
			throw new NotSupportedException("not support tcc: " + context.getMethod().toString());
		}

		if (!TransactionManager.hasTransaction()) {
			throw new NotSupportedException("not exist transaction");
		}

		Object result = filterChain.doFilter(invoker, context);
		final TccService tccService = instanceFactory.getInstance(tcc.service());
		final Stage confirm = tccService.createConfirm(context, result, tcc);
		final Stage cancel = tccService.createCancel(context, result, tcc);
		if (confirm == null && cancel == null) {
			throw new TccException("confirm or cancel At least one: " + context.getMethod().toString());
		}

		if ((confirm != null && !confirm.isActive()) || (cancel != null && !cancel.isActive())) {
			throw new TccException("tcc definition error:" + context.getMethod());
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
