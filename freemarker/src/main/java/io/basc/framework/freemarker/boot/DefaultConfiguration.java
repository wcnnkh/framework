package io.basc.framework.freemarker.boot;

import java.io.IOException;
import java.util.Map.Entry;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.annotation.Component;
import io.basc.framework.context.annotation.ConditionalOnMissingBean;
import io.basc.framework.freemarker.EnvConfiguration;
import io.basc.framework.freemarker.boot.annotation.SharedVariable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

@Component
@ConditionalOnMissingBean(DefaultConfiguration.class)
public class DefaultConfiguration extends EnvConfiguration {
	private static Logger logger = LoggerFactory.getLogger(DefaultConfiguration.class);

	public DefaultConfiguration(ApplicationContext context) throws IOException {
		super(context);
		for (Entry<String, Object> entry : context.getBeansWithAnnotation(SharedVariable.class).entrySet()) {
			String name = entry.getKey();
			TemplateModel registred = getSharedVariable(name);
			if (registred != null) {
				logger.warn("already exist name={}, registred={}", name, registred);
				continue;
			}

			Object veriable = entry.getValue();
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
