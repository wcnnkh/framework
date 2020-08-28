package scw.tcc;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import scw.aop.MethodInvoker;
import scw.beans.BeanFactory;
import scw.complete.RelyOnBeanFactoryCompleteTask;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.tcc.annotation.TryResult;

public class Stage extends RelyOnBeanFactoryCompleteTask {
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

		Method method = getMethod();
		if (method == null) {
			return false;
		}

		getArgs(method);
		return true;
	}

	public Method getMethod() {
		return tryInfo.getStageMethod(stageName);
	}

	public Object[] getArgs(Method method) {
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

			if (!parameterMap.containsKey(descriptor.getName())) {
				throw new TccException(
						"Undefined parameter [" + descriptor.getName() + "] in method:" + method.toString());
			}

			args[i] = parameterMap.get(descriptor.getName());
		}
		return args;
	}

	public Object process() throws Throwable {
		Method method = getMethod();
		ReflectionUtils.makeAccessible(method);
		MethodInvoker methodInvoker = getBeanFactory().getAop().getProxyMethod(getBeanFactory(), beanName,
				tryInfo.getTargetClass(), method);
		return methodInvoker.invoke(getArgs(method));
	}

	@Override
	public String toString() {
		Method method = getMethod();
		return "beanName=" + beanName + " stage=" + stageName + ", method=" + (method == null ? null : method);
	}
}
