package io.basc.framework.context.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import io.basc.framework.beans.factory.spi.SPI;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultServletContextInitializer extends ConfigurableServletContextInitializer {
	private final DefaultServletContextInitializeExtender extender = new DefaultServletContextInitializeExtender();

	public DefaultServletContextInitializer() {
		registerServiceLoader(SPI.global().getServiceLoader(ServletContextInitializer.class));
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		// 执行扩展
		extender.onStartup(servletContext, null);
	}
}
