package scw.beans.auto;

import java.util.Collection;
import java.util.Collections;

import scw.beans.BeanFactory;

public class SingleInstanceAutoBean extends AbstractAutoBean {
	private Object instance;

	public SingleInstanceAutoBean(BeanFactory beanFactory, Class<?> type, Object instance) {
		super(beanFactory, type);
		this.instance = instance;
	}

	public boolean isInstance() {
		return true;
	}

	public Object create() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<String> getFilterNames() {
		return Collections.EMPTY_LIST;
	}
}
