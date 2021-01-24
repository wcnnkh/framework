package scw.freemarker;

import java.io.IOException;

import scw.beans.BeanFactory;
import scw.context.annotation.Provider;
import scw.core.utils.StringUtils;
import scw.freemarker.annotation.SharedVariable;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

@Provider(order = Integer.MIN_VALUE, value = Configuration.class)
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
		for (Class<?> clz : beanFactory.getContextClassesLoader()) {
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
