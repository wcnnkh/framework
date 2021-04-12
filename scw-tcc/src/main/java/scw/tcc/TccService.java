package scw.tcc;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import scw.aop.MethodInterceptor;
import scw.aop.MethodInterceptorAccept;
import scw.beans.BeanUtils;
import scw.beans.RuntimeBean;
import scw.consistency.CompensateRegistry;
import scw.consistency.Compensator;
import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.MethodInvoker;
import scw.core.utils.StringUtils;
import scw.lang.NotSupportedException;
import scw.tcc.annotation.Tcc;
import scw.tcc.annotation.TccStage;
import scw.tcc.annotation.TryResult;
import scw.transaction.Transaction;
import scw.transaction.TransactionUtils;
import scw.util.XUtils;

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
			String oldId = transaction.bindResource(TccService.class, transactionId);
			if (oldId != null) {
				transactionId = oldId;
			}
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

		String oldId = transaction.bindResource(TccService.class, transactionId);
		return oldId == null ? transactionId : oldId;
	}

	private final CompensateRegistry compensatRegistry;

	public TccService(CompensateRegistry compensatRegistry) {
		this.compensatRegistry = compensatRegistry;
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
			throw new NotSupportedException("not exist transaction");
		}

		RuntimeBean runtimeBean = BeanUtils.getRuntimeBean(invoker.getInstance());
		if (runtimeBean == null) {
			throw new NotSupportedException("not exist transaction");
		}

		Object result = invoker.invoke(args);
		// 先注册一个取消任务，以防止最坏的情况发生，那样还可以回滚,但是如果存在confirm的情况下还会执行confirm，所以应该在业务中判断如果已经cancel了那么confirm无效
		Method confirmMethod = getStepMethod(invoker.getDeclaringClass(), tcc.confirm());
		Method cancelMethod = getStepMethod(invoker.getDeclaringClass(), tcc.cancel());

		String transactionId = getTransactionId(true);
		Compensator confirm = null;
		if (confirmMethod != null) {
			Object[] stepArgs = getStepArgs(invoker.getMethod(), result, args, confirmMethod);
			Stage stage = new Stage(invoker.getDeclaringClass(), confirmMethod, runtimeBean.getBeanDefinition().getId(),
					stepArgs);
			stage.setInstance(invoker.getInstance());
			confirm = compensatRegistry.register(transactionId, XUtils.getUUID(), stage);
		}

		Compensator cancel = null;
		if (cancelMethod != null) {
			Object[] stepArgs = getStepArgs(invoker.getMethod(), result, args, cancelMethod);
			Stage stage = new Stage(invoker.getDeclaringClass(), cancelMethod, runtimeBean.getBeanDefinition().getId(),
					stepArgs);
			stage.setInstance(invoker.getInstance());
			cancel = compensatRegistry.register(transactionId, XUtils.getUUID(), stage);
		}

		transaction.addLifecycle(new TccCompensator(confirm, cancel));
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
		ParameterDescriptor[] parameterDescriptors = ParameterUtils.getParameterDescriptors(stepMethod);
		if (parameterDescriptors.length == 0) {
			return new Object[0];
		}

		LinkedHashMap<String, Object> parameterMap = ParameterUtils.getParameterMap(tryMethod, tryArgs);
		Object[] args = new Object[parameterDescriptors.length];
		for (int i = 0; i < parameterDescriptors.length; i++) {
			ParameterDescriptor descriptor = parameterDescriptors[i];
			TryResult tryResultAnnotation = descriptor.getAnnotatedElement().getAnnotation(TryResult.class);
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
