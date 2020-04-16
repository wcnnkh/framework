package scw.freemarker.mvc;

import java.io.File;
import java.io.IOException;

import scw.beans.BeanFactory;
import scw.core.Constants;
import scw.core.GlobalPropertyFactory;
import scw.util.value.property.PropertyFactory;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

@scw.core.instance.annotation.Configuration(order=Integer.MIN_VALUE, value=Configuration.class)
public class DefaultConfiguration extends Configuration {

	public DefaultConfiguration(BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws IOException {
		super(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		setDefaultEncoding(Constants.DEFAULT_CHARSET_NAME);
		if (beanFactory.isInstance(TemplateLoader.class)) {
			setTemplateLoader(beanFactory.getInstance(TemplateLoader.class));
		} else {
			setDirectoryForTemplateLoading(new File(GlobalPropertyFactory
					.getInstance().getWorkPath()));
		}

		setObjectWrapper(new DefaultObjectWrapper(
				Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
	}
}
