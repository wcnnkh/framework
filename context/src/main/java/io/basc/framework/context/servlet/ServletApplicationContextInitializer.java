package io.basc.framework.context.servlet;

import javax.servlet.ServletContext;

import io.basc.framework.context.config.ApplicationContextInitializer;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import lombok.Data;

@Data
public class ServletApplicationContextInitializer implements ApplicationContextInitializer {
	private final ServletContext servletContext;

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		
		applicationContext.setClassLoader(servletContext.getClassLoader());
	}

}
