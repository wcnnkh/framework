package io.basc.framework.tcc;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.MethodInterceptorAccept;
import io.basc.framework.consistency.CompensateRegistry;
import io.basc.framework.consistency.Compensator;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.context.ioc.annotation.Autowired;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.factory.support.RuntimeBean;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterUtils;
import io.basc.framework.tcc.annotation.Tcc;
import io.basc.framework.tcc.annotation.TccStage;
import io.basc.framework.tcc.annotation.TryResult;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;

@Provider(order = Ordered.HIGHEST_PRECEDENCE)
public class TccService implements MethodInterceptor, MethodInterceptorAccept {
	/**
	 * 获取当前事务的id
	 * 
	 * @return
	 */
	public static String getTransactionId(boolean create) throws TccException {
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		if (transaction == null) {
			throw new TccException("not exist transaction");
		}

		String transactionId = transaction.getResource(TccService.class);
		if (transactionId == null && create) {
			transactionId = XUtils.getUUID();
			transaction.registerResource(TccService.class, transactionId);
		}
		return transactionId;
	}

	/**
	 * 将事务id绑定到当前事务，如果当前事务已经存在事务id那么返回已绑定的id，如果不存在就返回空
	 * 
	 * @param transactionId
	 * @return
	 */
	public static String bindTransactionId(String transactionId) throws TccException {
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		if (transaction == null) {
			throw new TccException("not exist transaction");
		}

		String oldId = transaction.getResource(TccService.class);
		if (oldId == null) {
			transaction.registerResource(TccService.class, transactionId);
			oldId = transactionId;
		}
		return oldId;
	}

	@Autowired(required = false)
	private CompensateRegistry compensateRegistry;

	public CompensateRegistry getCompensateRegistry() {
		return compensateRegistry;
	}

	public void setCompensateRegistry(CompensateRegistry compensateRegistry) {
		this.compensateRegistry = compensateRegistry;
	}

	public boolean isAccept(MethodInvoker invoker, Object[] args) {
		Tcc tcc = invoker.getMethod().getAnnotation(Tcc.class);
		return tcc != null;
	}

	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		Tcc tcc = invoker.getMethod().getAnnotation(Tcc.class);
		if (tcc == null) {
			return invoker.invoke(args);
		}

		Transaction transaction = TransactionUtils.getManager().getTransaction();
		if (transaction == null) {
			throw new UnsupportedException("not exist transaction");
		}

		if (compensateRegistry == null) {
			throw new UnsupportedException("not exist compensate registry");
		}

		RuntimeBean runtimeBean = RuntimeBean.getRuntimeBean(invoker.getInstance());
		if (runtimeBean == null) {
			throw new UnsupportedException("not exist transaction");
		}

		Object result = invoker.invoke(args);
		// 先注册一个取消任务，以防止最坏的情况发生，那样还可以回滚,但是如果存在confirm的情况下还会执行confirm，所以应该在业务中判断如果已经cancel了那么confirm无效
		Method confirmMethod = getStepMethod(invoker.getSourceClass(), tcc.confirm());
		Method cancelMethod = getStepMethod(invoker.getSourceClass(), tcc.cancel());

		String transactionId = getTransactionId(true);
		Compensator confirm = null;
		if (confirmMethod != null) {
			Object[] stepArgs = getStepArgs(invoker.getMethod(), result, args, confirmMethod);
			Stage stage = new Stage(confirmMethod, runtimeBean.getBeanDefinition().getId(), stepArgs);
			stage.setInstance(invoker.getInstance());
			confirm = compensateRegistry.register(transactionId, XUtils.getUUID(), stage);
		}

		Compensator cancel = null;
		if (cancelMethod != null) {
			Object[] stepArgs = getStepArgs(invoker.getMethod(), result, args, cancelMethod);
			Stage stage = new Stage(cancelMethod, runtimeBean.getBeanDefinition().getId(), stepArgs);
			stage.setInstance(invoker.getInstance());
			cancel = compensateRegistry.register(transactionId, XUtils.getUUID(), stage);
		}

		transaction.registerSynchronization(new TccSynchronization(confirm, cancel));
		return result;
	}

	public Method getStepMethod(Class<?> declaringClass, String stepName) {
		if (StringUtils.isEmpty(stepName)) {
			return null;
		}

		for (Method method : declaringClass.getDeclaredMethods()) {
			TccStage tccStage = method.getAnnotation(TccStage.class);
			if (tccStage == null) {
				continue;
			}

			String name = StringUtils.isEmpty(tccStage.value()) ? method.getName() : tccStage.value();
			if (stepName.equals(name)) {
				return method;
			}
		}
		return null;
	}

	public Object[] getStepArgs(Method tryMethod, Object tryResult, Object[] tryArgs, Method stepMethod) {
		ParameterDescriptor[] parameterDescriptors = ParameterUtils.getParameters(stepMethod);
		if (parameterDescriptors.length == 0) {
			return new Object[0];
		}

		LinkedHashMap<String, Object> parameterMap = ParameterUtils.getParameterMap(tryMethod, tryArgs);
		Object[] args = new Object[parameterDescriptors.length];
		for (int i = 0; i < parameterDescriptors.length; i++) {
			ParameterDescriptor descriptor = parameterDescriptors[i];
			TryResult tryResultAnnotation = descriptor.getAnnotation(TryResult.class);
			if (tryResultAnnotation != null) {
				args[i] = tryResult;
				continue;
			}

			if (!parameterMap.containsKey(descriptor.getName())) {
				throw new TccException(
						"Undefined parameter [" + descriptor.getName() + "] in method:" + stepMethod.toString());
			}

			args[i] = parameterMap.get(descriptor.getName());
		}
		return args;
	}
}
