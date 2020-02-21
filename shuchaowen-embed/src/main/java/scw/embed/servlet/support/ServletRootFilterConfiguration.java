package scw.embed.servlet.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.Filter;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.PropertyFactory;
import scw.embed.servlet.FilterConfiguration;

public class ServletRootFilterConfiguration implements FilterConfiguration {
	private List<Filter> filters;

	public ServletRootFilterConfiguration(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		this.filters = BeanUtils.getConfigurationList(Filter.class, null,
				beanFactory, propertyFactory);
	}

	public String getName() {
		return getClass().getSimpleName();
	}

	public Collection<? extends String> getURLPatterns() {
		return Arrays.asList("/*");
	}

	public Collection<? extends Filter> getFilters() {
		return filters;
	}

}
