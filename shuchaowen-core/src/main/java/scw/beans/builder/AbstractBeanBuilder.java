package scw.beans.builder;

import java.lang.reflect.Constructor;
import java.util.LinkedList;

import scw.aop.FilterChain;
import scw.aop.InstanceFactoryFilterChain;
import scw.aop.Proxy;
import scw.beans.BeanFactory;
import scw.beans.BeanMethod;
import scw.beans.BeanUtils;
import scw.core.instance.AbstractInstanceBuilder;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

public abstract class AbstractBeanBuilder extends
		AbstractInstanceBuilder<Object> implements BeanBuilder {
	protected final Logger logger = LoggerUtils.getLogger(getClass());

	protected final BeanFactory beanFactory;
	protected final PropertyFactory propertyFactory;
	protected final LinkedList<String> filterNames = new LinkedList<String>();
	protected final LinkedList<BeanMethod> initMethods = new LinkedList<BeanMethod>();
	protected final LinkedList<BeanMethod> destroyMethods = new LinkedList<BeanMethod>();
	protected FilterChain filterChain;

	public AbstractBeanBuilder(LoaderContext context) {
		this(context.getBeanFactory(), context.getPropertyFactory(), context
				.getTargetClass());
	}

	public AbstractBeanBuilder(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass) {
		super(targetClass);
		this.propertyFactory = propertyFactory;
		this.beanFactory = beanFactory;
	}

	protected boolean isProxy() {
		return BeanUtils.isProxy(getTargetClass(), getTargetClass());
	}

	@Override
	protected Object createInternal(Class<?> targetClass,
			Constructor<? extends Object> constructor, Object[] params)
			throws Exception {
		if (isProxy()) {
			return createProxyInstance(targetClass,
					constructor.getParameterTypes(), params);
		}
		return super.createInternal(targetClass, constructor, params);
	}

	protected Proxy createProxy(Class<?> targetClass, Class<?>[] interfaces) {
		return beanFactory.getAop().proxy(
				targetClass,
				interfaces,
				new InstanceFactoryFilterChain(beanFactory, filterNames,
						filterChain));
	}

	protected Proxy createInstanceProxy(Object instance, Class<?> targetClass,
			Class<?>[] interfaces) {
		return beanFactory.getAop().proxyInstance(
				targetClass,
				instance,
				interfaces,
				new InstanceFactoryFilterChain(beanFactory, filterNames,
						filterChain));
	}

	protected Object createProxyInstance(Class<?> targetClass,
			Class<?>[] parameterTypes, Object[] args) {
		if (getTargetClass().isInterface() && filterNames.isEmpty()
				&& filterChain == null) {
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
