package scw.beans.auto;

public class SimpleAutoBeanConfig implements AutoBeanConfig {
	private String[] filters;

	public SimpleAutoBeanConfig(String[] filters) {
		this.filters = filters;
	}

	public String[] getFilters() {
		return filters;
	}

}
