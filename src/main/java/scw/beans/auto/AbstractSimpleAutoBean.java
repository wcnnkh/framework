package scw.beans.auto;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.utils.CollectionUtils;
import scw.lang.NotSupportException;

public abstract class AbstractSimpleAutoBean extends AbstractAutoBean {

	public AbstractSimpleAutoBean(BeanFactory beanFactory, Class<?> type) {
		super(beanFactory, type);
	}

	public boolean isInstance() {
		return getParameterTypes() != null;
	}

	protected abstract Class<?>[] getParameterTypes();

	protected abstract Object[] getParameters();

	@SuppressWarnings("unchecked")
	public <T> T create() {
		if (!isInstance()) {
			throw new NotSupportException(type.getName());
		}

		if (type.isInterface()) {
			if (!CollectionUtils.isEmpty(getFilterNames())) {
				return (T) BeanUtils.proxyInterface(beanFactory, type, getFilterNames(), null);
			}
			throw new NotSupportException(type.getName());
		}

		return create(getParameterTypes(), getParameters());
	}
}
