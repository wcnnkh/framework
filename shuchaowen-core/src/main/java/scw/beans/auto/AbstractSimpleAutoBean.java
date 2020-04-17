package scw.beans.auto;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.utils.CollectionUtils;
import scw.lang.UnsupportedException;

public abstract class AbstractSimpleAutoBean extends AbstractAutoBean {

	public AbstractSimpleAutoBean(BeanFactory beanFactory, Class<?> type) {
		super(beanFactory, type);
	}

	public boolean isInstance() {
		return getParameterTypes() != null;
	}

	protected abstract Class<?>[] getParameterTypes();
 
	protected abstract Object[] getParameters() throws Exception;

	@SuppressWarnings("unchecked")
	public <T> T create() throws Exception{
		if (!isInstance()) {
			throw new UnsupportedException(type.getName());
		}

		if (type.isInterface()) {
			if (!CollectionUtils.isEmpty(getFilterNames())) {
				return (T) BeanUtils.createProxy(beanFactory, type, getFilterNames(), null).create();
			}
			throw new UnsupportedException(type.getName());
		}

		return create(getParameterTypes(), getParameters());
	}
}
