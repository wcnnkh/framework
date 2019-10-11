package scw.beans.auto;

import scw.beans.BeanFactory;
import scw.core.aop.Filter;

public class ProxyAutoBean extends AbstractAutoBean {
	private String proxyName;

	public ProxyAutoBean(BeanFactory beanFactory, Class<?> type, String proxyName) {
		super(beanFactory, type);
		this.proxyName = proxyName;
	}

	@Override
	protected Class<?>[] getParameterTypes() {
		return new Class[0];
	}

	@Override
	protected Object[] getParameters() {
		return new Object[0];
	}

	@Override
	protected Filter getLastFilter() {
		return (Filter) (beanFactory.isInstance(proxyName) ? beanFactory.getInstance(proxyName) : null);
	}
}
