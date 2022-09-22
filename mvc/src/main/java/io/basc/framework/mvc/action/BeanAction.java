package io.basc.framework.mvc.action;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.function.Supplier;

import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.supplier.NameInstanceSupplier;
import io.basc.framework.factory.supplier.SimpleInstanceSupplier;
import io.basc.framework.factory.support.InstanceIterable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mvc.annotation.ActionInterceptors;
import io.basc.framework.web.pattern.HttpPatternResolver;

public class BeanAction extends AbstractAction {
	private static Logger logger = LoggerFactory.getLogger(BeanAction.class);
	private final BeanFactory beanFactory;
	private final MethodInvoker invoker;
	private Iterable<ActionInterceptor> actionInterceptors;

	/**
	 * @param beanFactory
	 * @param targetClass
	 * @param method
	 */
	public BeanAction(BeanFactory beanFactory, Class<?> targetClass, Method method, HttpPatternResolver httpPatternResolver) {
		super(targetClass, method, httpPatternResolver);
		this.beanFactory = beanFactory;
		Supplier<Object> instanceSupplier;
		if(beanFactory.isSingleton(targetClass)){
			instanceSupplier = new SimpleInstanceSupplier<Object>(beanFactory.getInstance(targetClass));
		}else{
			instanceSupplier = new NameInstanceSupplier<Object>(beanFactory, targetClass.getName());
		}
		this.invoker = beanFactory.getAop().getProxyMethod(targetClass, instanceSupplier, method);
		String[] names = getActionInterceptorNames();
		this.actionInterceptors = new InstanceIterable<ActionInterceptor>(beanFactory, Arrays.asList(names));
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
	
	public Iterable<ActionInterceptor> getActionInterceptors() {
		return actionInterceptors;
	}

	public Object invoke(Object... args) throws Throwable {
		return invoker.invoke(args);
	}

	public Object getInstance() {
		return invoker.getInstance();
	}
	
	protected String[] getActionInterceptorNames() {
		LinkedHashSet<String> sets = new LinkedHashSet<String>();
		ActionInterceptors actionInterceptors = getSourceClass().getAnnotation(
				ActionInterceptors.class);
		if (actionInterceptors != null) {
			for (Class<? extends ActionInterceptor> f : actionInterceptors.value()) {
				BeanDefinition definition = getBeanFactory().getDefinition(f);
				if(definition == null){
					logger.warn("not support interceptor: {}", f);
					continue;
				}

				sets.remove(definition.getId());
				sets.add(definition.getId());
			}
		}

		actionInterceptors = getMethod().getAnnotation(ActionInterceptors.class);
		if (actionInterceptors != null) {
			sets.clear();
			for (Class<? extends ActionInterceptor> f : actionInterceptors.value()) {
				BeanDefinition definition = getBeanFactory().getDefinition(f);
				if(definition == null){
					logger.warn("not support interceptor: {}", f);
					continue;
				}

				sets.remove(definition.getId());
				sets.add(definition.getId());
			}
		}
		return sets.toArray(new String[0]);
	}
}
