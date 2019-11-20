package scw.beans.auto;

import java.util.Collection;
import java.util.Collections;

import scw.beans.BeanFactory;

public class ProxyAutoBean extends AbstractSimpleAutoBean {
	private Collection<String> proxyNames;

	public ProxyAutoBean(BeanFactory beanFactory, Class<?> type,
			Collection<String> proxyNames) {
		super(beanFactory, type);
		this.proxyNames = proxyNames;
	}

	@Override
	protected Class<?>[] getParameterTypes() {
		return new Class[0];
	}

	@Override
	protected Object[] getParameters() {
		return new Object[0];
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<String> getFilterNames() {
		return proxyNames == null ? Collections.EMPTY_LIST : proxyNames;
	}
}
