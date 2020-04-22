package scw.transaction.tcc;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import scw.aop.MethodInvoker;
import scw.async.AbstractAsyncRunnable;
import scw.beans.BeanFactory;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.transaction.tcc.annotation.TryResult;

public class Stage extends AbstractAsyncRunnable {
	private static final long serialVersionUID = 1L;
	private final TryInfo tryInfo;
	private final String beanName;
	private final String stageName;

	public Stage(BeanFactory beanFactory, String beanName, TryInfo tryInfo, String stageName) {
		setBeanFactory(beanFactory);
		this.beanName = beanName;
		this.tryInfo = tryInfo;
		this.stageName = stageName;
	}

	public boolean isActive() {
		if (getBeanFactory() == null) {
			return false;
		}

		if (!getBeanFactory().isInstance(beanName)) {
			return false;
		}

		if (getMethod() == null) {
			return false;
		}

		return true;
	}

	public Method getMethod() {
		return tryInfo.getStageMethod(stageName);
	}

	protected Object[] getArgs(Method method) {
		ParameterDescriptor[] parameterDescriptors = ParameterUtils.getParameterDescriptors(method);
		if (parameterDescriptors.length == 0) {
			return new Object[0];
		}

		LinkedHashMap<String, Object> parameterMap = tryInfo.getParameterMap();
		Object[] args = new Object[parameterDescriptors.length];
		for (int i = 0; i < parameterDescriptors.length; i++) {
			ParameterDescriptor descriptor = parameterDescriptors[i];
			TryResult tryResult = descriptor.getAnnotatedElement().getAnnotation(TryResult.class);
			if (tryResult != null) {
				args[i] = tryInfo.getTryResult();
				continue;
			}

			if (!parameterMap.containsKey(descriptor.getDisplayName())) {
				throw new TccException(
						"Undefined parameter [" + descriptor.getDisplayName() + "] in method:" + method.toString());
			}

			args[i] = parameterMap.get(descriptor.getDisplayName());
		}
		return args;
	}

	public Object call() throws Exception {
		Method method = getMethod();
		ReflectionUtils.setAccessibleMethod(method);
		MethodInvoker methodInvoker = getBeanFactory().getAop().proxyMethod(getBeanFactory(), beanName,
				tryInfo.getTargetClass(), method, null, null);
		try {
			return methodInvoker.invoke(getArgs(method));
		} catch (Throwable e) {
			throw new TccException(method.toString(), e);
		}
	}
}
