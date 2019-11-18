package scw.beans.auto;

import java.util.Arrays;
import java.util.Collection;

import scw.beans.BeanFactory;

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
	protected Collection<String> getFilterNames() {
		return Arrays.asList(proxyName);
	}
}
