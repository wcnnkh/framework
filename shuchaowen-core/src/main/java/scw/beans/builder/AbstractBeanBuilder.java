package scw.beans.builder;

import java.lang.reflect.Constructor;
import java.util.LinkedList;

import scw.aop.InstanceFactoryFilterChain;
import scw.aop.Proxy;
import scw.beans.BeanFactory;
import scw.beans.BeanMethod;
import scw.beans.BeanUtils;
import scw.core.instance.AbstractInstanceBuilder;
import scw.lang.UnsupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

public abstract class AbstractBeanBuilder extends AbstractInstanceBuilder<Object> implements BeanBuilder {
	protected final Logger logger = LoggerUtils.getLogger(getClass());

	protected final BeanFactory beanFactory;
	protected final PropertyFactory propertyFactory;
	protected final LinkedList<String> filterNames = new LinkedList<String>();
	protected final LinkedList<BeanMethod> initMethods = new LinkedList<BeanMethod>();
	protected final LinkedList<BeanMethod> destroyMethods = new LinkedList<BeanMethod>();

	public AbstractBeanBuilder(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass) {
		super(targetClass);
		this.propertyFactory = propertyFactory;
		this.beanFactory = beanFactory;
	}

	protected boolean isProxy() {
		if (getTargetClass().isInterface()) {
			return true;
		}

		if (filterNames.isEmpty()) {
			return false;
		}

		return BeanUtils.isProxy(getTargetClass(), getTargetClass());
	}
	
	protected Proxy createProxy(Class<?> targetClass){
		return beanFactory.getAopProxyAdapter().proxy(targetClass, null, null,
				new InstanceFactoryFilterChain(beanFactory, filterNames, null));
	}

	@Override
	protected Object createInternal(Class<?> targetClass, Constructor<? extends Object> constructor, Object[] params)
			throws Exception {
		if (getTargetClass().isInterface() && filterNames.isEmpty()) {
			throw new UnsupportedException(getTargetClass().getName());
		}

		if (isProxy()) {
			Proxy proxy = beanFactory.getAopProxyAdapter().proxy(targetClass, null, null,
					new InstanceFactoryFilterChain(beanFactory, filterNames, null));
			return proxy.create(constructor.getParameterTypes(), params);
		}
		return super.createInternal(targetClass, constructor, params);
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
