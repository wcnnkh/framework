package scw.transaction.tcc;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.aop.ProxyContext;
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

		TransactionManager.transactionLifeCycle(new TransactionLifeCycle() {

			public void complete() {
			}

			public void beforeRollback() {
				tccService.execute(cancel);
			}

			public void beforeProcess() throws Throwable {
				tccService.execute(confirm);
			}

			public void afterRollback() {

			}

			public void afterProcess() {
			}
		});
		return result;
	}

}
