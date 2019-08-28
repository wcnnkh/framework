package scw.beans.auto;

public class SimpleAutoBeanConfig implements AutoBeanConfig {
	private boolean proxy;
	private String[] filters;

	public SimpleAutoBeanConfig(boolean proxy, String[] filters) {
		this.proxy = proxy;
		this.filters = filters;
	}

	public boolean isProxy() {
		return proxy;
	}

	public String[] getFilters() {
		return filters;
	}

}
