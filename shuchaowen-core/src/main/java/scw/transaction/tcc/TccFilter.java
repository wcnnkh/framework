package scw.transaction.tcc;

import java.lang.reflect.Method;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.core.instance.NoArgsInstanceFactory;
import scw.core.instance.annotation.Configuration;
import scw.lang.UnsupportedException;
import scw.transaction.TransactionLifeCycle;
import scw.transaction.TransactionManager;
import scw.transaction.tcc.annotation.Tcc;

@Configuration(order = Integer.MAX_VALUE)
public class TccFilter implements Filter {
	private NoArgsInstanceFactory instanceFactory;

	public TccFilter(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args,
			FilterChain filterChain) throws Throwable {
		final Tcc tcc = method.getAnnotation(Tcc.class);
		if (tcc == null) {
			return filterChain.doFilter(invoker, proxy, targetClass, method, args);
		}

		if (!instanceFactory.isInstance(tcc.service())) {
			throw new UnsupportedException("not support tcc: " + method.toString());
		}

		if (!TransactionManager.hasTransaction()) {
			throw new UnsupportedException("not exist transaction");
		}

		Object result = filterChain.doFilter(invoker, proxy, targetClass, method, args);
		final TccService tccService = instanceFactory.getInstance(tcc.service());
		final Stage confirm = tccService.createConfirm(targetClass, method, result, args, tcc);
		final Stage cancel = tccService.createCancel(targetClass, method, result, args, tcc);
		if (confirm == null && cancel == null) {
			throw new TccException("confirm or cancel At least one: " + method.toString());
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
