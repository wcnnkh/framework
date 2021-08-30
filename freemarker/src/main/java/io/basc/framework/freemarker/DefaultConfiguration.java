package io.basc.framework.freemarker;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.freemarker.annotation.SharedVariable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StringUtils;

import java.io.IOException;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

@Provider(value = Configuration.class)
public class DefaultConfiguration extends Configuration {
	private static Logger logger = LoggerFactory.getLogger(DefaultConfiguration.class);

	public DefaultConfiguration(BeanFactory beanFactory) throws IOException {
		super(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		setDefaultEncoding(beanFactory.getEnvironment().getCharsetName());
		if (beanFactory.isInstance(TemplateLoader.class)) {
			setTemplateLoader(beanFactory.getInstance(TemplateLoader.class));
		} else {
			setTemplateLoader(new DefaultTemplateLoader(beanFactory.getEnvironment()));
		}
		if (beanFactory.isInstance(TemplateExceptionHandler.class)) {
			setTemplateExceptionHandler(beanFactory.getInstance(TemplateExceptionHandler.class));
		}

		setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
		for (Class<?> clz : beanFactory.getContextClasses()) {
			SharedVariable sharedVariable = clz.getAnnotation(SharedVariable.class);
			if (sharedVariable == null) {
				continue;
			}

			String name = sharedVariable.value();
			if (StringUtils.isEmpty(name)) {
				// 默认使用简写类名
				name = clz.getSimpleName();
			}

			TemplateModel registred = getSharedVariable(name);
			if (registred != null) {
				logger.warn("already exist name={}, registred={}", name, registred);
				continue;
			}

			Object veriable = beanFactory.getInstance(clz);
			if (veriable instanceof TemplateModel) {
				setSharedVariable(name, (TemplateModel) veriable);
			} else {
				try {
					setSharedVariable(name, veriable);
				} catch (TemplateModelException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
