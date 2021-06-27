package scw.mvc.action;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.core.reflect.MethodInvoker;
import scw.instance.supplier.NameInstanceSupplier;
import scw.instance.supplier.SimpleInstanceSupplier;
import scw.instance.support.InstanceIterable;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.HttpPatternResolver;
import scw.mvc.annotation.ActionInterceptors;
import scw.mvc.annotation.Controller;
import scw.util.Supplier;

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
			//如果是单例就不延迟加载
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
		ActionInterceptors actionInterceptors = getDeclaringClass().getAnnotation(
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

		Controller controller = getDeclaringClass().getAnnotation(
				Controller.class);
		if (controller != null) {
			for (Class<?> f : controller.interceptors()) {
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

		controller = getMethod().getAnnotation(Controller.class);
		if (controller != null) {
			for (Class<?> f : controller.interceptors()) {
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
