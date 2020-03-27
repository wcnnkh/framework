package scw.freemarker.mvc;

import java.io.IOException;

import scw.beans.BeanFactory;
import scw.beans.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.mvc.page.PageFactoryAdapter;
import scw.util.value.property.PropertyFactory;

@Configuration(order=Integer.MIN_VALUE)
public final class FreemarkerPageFactoryAdapter extends FreemarkerPageFactory implements PageFactoryAdapter{

	public FreemarkerPageFactoryAdapter(BeanFactory beanFactory, PropertyFactory propertyFactory) throws IOException {
		super();
	}

	public boolean isAdapte(String page) {
		return StringUtils.endsWithIgnoreCase(page, ".ftl") || StringUtils.endsWithIgnoreCase(page, ".html");
	}
	
}
