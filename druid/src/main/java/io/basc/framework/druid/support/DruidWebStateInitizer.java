package io.basc.framework.druid.support;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;

import com.alibaba.druid.support.http.WebStatFilter;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.servlet.ServletContextInitialization;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class DruidWebStateInitizer implements ServletContextInitialization {
	private static Logger logger = LoggerFactory.getLogger(DruidWebStateInitizer.class);

	@Override
	public void init(Application application, ServletContext servletContext) {
		if (!application.getEnvironment().getBooleanValue("druid.web.stat.enable")) {
			return;
		}

		logger.info("Enable druid web stat!");
		Dynamic dynamic = servletContext.addFilter("DruidWebStatFilter", application.getInstance(WebStatFilter.class));
		// 经常需要排除一些不必要的url，比如.js,/jslib/等等。配置在init-param中
		dynamic.setInitParameter("exclusions", "/druid/*");

		application.getEnvironment().streamByPrefix("druid.web.stat.").forEach((e) -> {
			dynamic.setInitParameter(e.getKey(), e.getValue().getAsString());
		});
		dynamic.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
	}

}
