package scw.beans;

import java.util.LinkedList;

import scw.aop.InstanceFactoryFilterChain;
import scw.aop.Proxy;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

public abstract class AbstractBeanBuilder implements BeanBuilder {
	protected final Logger logger = LoggerUtils.getLogger(getClass());

	protected final BeanFactory beanFactory;
	protected final PropertyFactory propertyFactory;
	protected final LinkedList<String> filterNames = new LinkedList<String>();
	protected final LinkedList<BeanMethod> initMethods = new LinkedList<BeanMethod>();
	protected final LinkedList<BeanMethod> destroyMethods = new LinkedList<BeanMethod>();
	private final Class<?> targetClass;

	public AbstractBeanBuilder(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass) {
		this.targetClass = targetClass;
		this.propertyFactory = propertyFactory;
		this.beanFactory = beanFactory;
	}

	protected boolean isProxy() {
		return BeanUtils.isProxy(getTargetClass(), getTargetClass());
	}

	public Class<? extends Object> getTargetClass() {
		return targetClass;
	}

	protected Proxy createProxy(Class<?> targetClass, Class<?>[] interfaces) {
		return beanFactory.getAop().proxy(targetClass, interfaces,
				new InstanceFactoryFilterChain(beanFactory, filterNames, null));
	}

	protected Proxy createInstanceProxy(Object instance, Class<?> targetClass,
			Class<?>[] interfaces) {
		return beanFactory.getAop().proxyInstance(targetClass, instance,
				interfaces,
				new InstanceFactoryFilterChain(beanFactory, filterNames, null));
	}

	protected Object createProxyInstance(Class<?> targetClass,
			Class<?>[] parameterTypes, Object[] args) {
		if (getTargetClass().isInterface() && filterNames.isEmpty()) {
			logger.warn("empty filter: {}", getTargetClass().getName());
		}

		Proxy proxy = createProxy(targetClass, null);
		return proxy.create(parameterTypes, args);
	}

	public void init(Object instance) throws Exception {
		for (BeanMethod beanMethod : initMethods) {
			beanMethod.invoke(instance, beanFactory, propertyFactory);
		}
	}

	public void destroy(Object instance) throws Exception {
		for (BeanMethod method : destroyMethods) {
			method.invoke(instance, beanFactory, propertyFactory);
		}
	}
}
