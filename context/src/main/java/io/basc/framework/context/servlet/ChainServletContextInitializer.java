package io.basc.framework.context.servlet;

import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
public class ChainServletContextInitializer implements ServletContextInitializer {
	@NonNull
	private final Iterator<? extends ServletContextInitializeExtender> iterator;
	private ServletContextInitializer nextChain;

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		if (iterator.hasNext()) {
			iterator.next().onStartup(servletContext, this);
		} else if (nextChain != null) {
			nextChain.onStartup(servletContext);
		}
	}

}
