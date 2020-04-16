package scw.freemarker.mvc;

import java.io.File;
import java.io.IOException;

import scw.beans.BeanFactory;
import scw.core.Constants;
import scw.core.GlobalPropertyFactory;
import scw.core.utils.ClassUtils;
import scw.freemarker.MultiTemplateLoader;
import scw.util.value.property.PropertyFactory;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;

@scw.core.instance.annotation.Configuration(order = Integer.MIN_VALUE, value = Configuration.class)
public class DefaultConfiguration extends Configuration {

	public DefaultConfiguration(BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws IOException {
		super(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		setDefaultEncoding(Constants.DEFAULT_CHARSET_NAME);
		if (beanFactory.isInstance(TemplateLoader.class)) {
			setTemplateLoader(beanFactory.getInstance(TemplateLoader.class));
		} else {
			setTemplateLoader(getDefaultTemplateLoader(beanFactory,
					propertyFactory));
		}
		if (beanFactory.isInstance(TemplateExceptionHandler.class)) {
			setTemplateExceptionHandler(beanFactory
					.getInstance(TemplateExceptionHandler.class));
		}

		setObjectWrapper(new DefaultObjectWrapper(
				Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
	}

	protected TemplateLoader getDefaultTemplateLoader(BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws IOException {
		MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader();
		multiTemplateLoader.add(new FileTemplateLoader(new File(
				GlobalPropertyFactory.getInstance().getWorkPath())));
		multiTemplateLoader.add(new ClassTemplateLoader(ClassUtils
				.getDefaultClassLoader(), "/"));
		return multiTemplateLoader;
	}
}
