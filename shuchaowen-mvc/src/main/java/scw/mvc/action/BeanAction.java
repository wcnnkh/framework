package scw.mvc.action;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.aop.Invoker;
import scw.beans.AutoProxyMethodInvoker;
import scw.beans.BeanFactory;
import scw.core.utils.CollectionUtils;
import scw.mvc.action.filter.ActionFilter;

public class BeanAction extends AbstractAction {
	private final BeanFactory beanFactory;
	private final Invoker invoker;

	public BeanAction(BeanFactory beanFactory, Class<?> targetClass,
			Method method) {
		this(beanFactory, targetClass, method, null);
	}

	public BeanAction(BeanFactory beanFactory, Class<?> targetClass,
			Method method, Collection<ActionFilter> actionFilters) {
		super(targetClass, method);
		this.beanFactory = beanFactory;
		if (!CollectionUtils.isEmpty(actionFilters)) {
			this.actionFilters.addAll(actionFilters);
		}

		this.invoker = new AutoProxyMethodInvoker(beanFactory, targetClass,
				method);
	}

	public Invoker getInvoker() {
		return invoker;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
