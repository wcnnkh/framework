package scw.beans.builder;

import java.lang.reflect.Constructor;
import java.util.LinkedList;

import scw.aop.Filter;
import scw.aop.Proxy;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.ioc.Ioc;
import scw.core.instance.AbstractInstanceBuilder;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.value.property.PropertyFactory;

public abstract class AbstractBeanBuilder extends
		AbstractInstanceBuilder<Object> implements BeanBuilder {
	protected final Logger logger = LoggerUtils.getLogger(getClass());

	protected final BeanFactory beanFactory;
	protected final PropertyFactory propertyFactory;
	protected final Ioc ioc = new Ioc();
	protected final LinkedList<Filter> filters = new LinkedList<Filter>();

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
		return BeanUtils.isAopEnable(getTargetClass(), getTargetClass());
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
		return beanFactory.getAop().getProxy(targetClass, interfaces, filters);
	}

	protected Proxy createInstanceProxy(Object instance, Class<?> targetClass,
			Class<?>[] interfaces) {
		return beanFactory.getAop().getProxyInstance(
				targetClass,
				instance,
				interfaces, filters);
	}

	protected Object createProxyInstance(Class<?> targetClass,
			Class<?>[] parameterTypes, Object[] args) {
		if (getTargetClass().isInterface() && filters.isEmpty()) {
			logger.warn("empty filter: {}", getTargetClass().getName());
		}

		Proxy proxy = createProxy(targetClass, null);
		return proxy.create(parameterTypes, args);
	}
	
	public void dependence(Object instance) throws Exception {
		ioc.getDependence().process(instance, beanFactory, propertyFactory, false);
	}

	public void init(Object instance) throws Exception {
		ioc.getInit().process(instance, beanFactory, propertyFactory, false);
	}

	public void destroy(Object instance) throws Exception {
		ioc.getDestroy().process(instance, beanFactory, propertyFactory, false);
	}
}
