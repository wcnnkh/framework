package scw.beans.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.core.parameter.ExecutableParameterDescriptors;
import scw.core.parameter.ExecutableParameterDescriptorsIterator;
import scw.core.parameter.ParameterDescriptors;
import scw.core.reflect.ReflectionUtils;
import scw.lang.NotSupportedException;

public class MethodBeanDefinition extends DefaultBeanDefinition {
	private final Method method;
	private final Class<?> methodTargetClass;
	private final ParameterDescriptors parameterDescriptors;
	
	public MethodBeanDefinition(ConfigurableBeanFactory beanFactory, Class<?> methodTargetClass,
			Method method) {
		super(beanFactory, method.getReturnType());
		this.methodTargetClass = methodTargetClass;
		this.method = method;
		this.parameterDescriptors = new ExecutableParameterDescriptors(methodTargetClass, method);
	}

	@Override
	public AnnotatedElement getAnnotatedElement() {
		return method;
	}

	private final AtomicBoolean error = new AtomicBoolean();
	public boolean isInstance() {
		boolean accept = isAccept(parameterDescriptors);
		if(!accept){
			if(!error.get() && error.compareAndSet(false, true)){
				logger.error("not found {} accept method {}", this, method);
			}
		}
		return accept;
	}

	public Object create() throws BeansException {
		if (!isInstance()) {
			throw new NotSupportedException("不支持的构造方式");
		}
		
		return createInternal(methodTargetClass, parameterDescriptors, getParameters(parameterDescriptors));
	}
	
	@Override
	protected Object createInternal(Class<?> targetClass,
			ParameterDescriptors parameterDescriptors, Object[] params) {
		Method method = ReflectionUtils.findMethod(targetClass, this.method.getName(), parameterDescriptors.getTypes());
		ReflectionUtils.makeAccessible(method);
		Object bean = ReflectionUtils.invokeMethod(method, Modifier.isStatic(method.getModifiers()) ? null : beanFactory.getInstance(methodTargetClass), params);
		if(beanFactory.getAop().isProxy(bean)){
			//已经被代理过的
			return bean;
		}
		
		//必须要是接口，因为非接口不一定是无法保证一定可以代理实例
		if (method.getReturnType().isInterface() && (isAopEnable(method.getReturnType(), method) || isAopEnable(bean.getClass(), method))) {
			return createInstanceProxy(bean, getTargetClass(), null).create();
		}
		return bean;
	}
	
	@Override
	public boolean isAopEnable() {
		//必须要是接口，因为非接口不一定是无法保证一定可以代理实例
		return getTargetClass().isInterface() && super.isAopEnable();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getTargetClass() {
		return method.getReturnType();
	}

	@Override
	public Iterator<ParameterDescriptors> iterator() {
		return new ExecutableParameterDescriptorsIterator(methodTargetClass, method, true);
	}

	public Method getMethod() {
		return method;
	}

	public Class<?> getMethodTargetClass() {
		return methodTargetClass;
	}
}
