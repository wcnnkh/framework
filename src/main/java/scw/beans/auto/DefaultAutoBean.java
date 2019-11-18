package scw.beans.auto;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.utils.ClassUtils;

public class DefaultAutoBean extends AbstractAutoBean {
	private Class<?>[] parameterTypes;
	private Object[] parameters;

	public DefaultAutoBean(BeanFactory beanFactory, Class<?> type, Class<?>[] parameterTypes, Object[] parameters) {
		super(beanFactory, type);
		this.parameterTypes = parameterTypes;
		this.parameters = parameters;
	}

	public DefaultAutoBean(BeanFactory beanFactory, String className, Class<?>[] parameterTypes, Object[] parameters)
			throws ClassNotFoundException {
		this(beanFactory, ClassUtils.forName(className), parameterTypes, parameters);
	}

	public DefaultAutoBean(BeanFactory beanFactory, Class<?> type) {
		this(beanFactory, type, new Class<?>[0], new Object[0]);
	}

	public DefaultAutoBean(BeanFactory beanFactory, String className) throws ClassNotFoundException {
		this(beanFactory, ClassUtils.forName(className), new Class<?>[0], new Object[0]);
	}

	@Override
	protected Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	@Override
	protected Object[] getParameters() {
		return parameters;
	}
	
	@Override
	protected Collection<String> getFilterNames() {
		return null;
	}
}
