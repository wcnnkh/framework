package io.basc.framework.freemarker.boot;

import java.io.IOException;

import freemarker.template.Configuration;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.freemarker.EnvConfiguration;
import io.basc.framework.freemarker.boot.annotation.SharedVariable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StringUtils;

@Provider(value = Configuration.class)
public class DefaultConfiguration extends EnvConfiguration {
	private static Logger logger = LoggerFactory.getLogger(DefaultConfiguration.class);

	public DefaultConfiguration(BeanFactory beanFactory) throws IOException {
		super(beanFactory.getEnvironment());
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
