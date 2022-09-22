package io.basc.framework.druid.support;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.servlet.ServletContextInitialization;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.RandomUtils;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class DruidWebStateInitizer implements ServletContextInitialization {
	private static Logger logger = LoggerFactory.getLogger(DruidWebStateInitizer.class);

	@Override
	public void init(Application application, ServletContext servletContext) {
		String value = application.getProperties().getString("druid.filters");
		if (value == null || !value.contains("stat")) {
			return;
		}

		logger.info("Enable druid web stat!");
		javax.servlet.ServletRegistration.Dynamic servletDynamic = servletContext.addServlet("DruidStatView",
				StatViewServlet.class);
		servletDynamic.addMapping("/druid/*");

		Dynamic filterDynamic = servletContext.addFilter("DruidWebStatFilter",
				application.getInstance(WebStatFilter.class));
		// 经常需要排除一些不必要的url，比如.js,/jslib/等等。配置在init-param中
		filterDynamic.setInitParameter("exclusions", "/druid/*");
		filterDynamic.setInitParameter("loginUsername", RandomUtils.randomString(10));
		filterDynamic.setInitParameter("loginPassword", RandomUtils.randomString(10));
		application.getProperties().streamByPrefix("druid.web.stat.").forEach((e) -> {
			filterDynamic.setInitParameter(e.getKey(), e.getValue().getAsString());
		});
		logger.info("Druid web state username[{}] password[{}]", filterDynamic.getInitParameter("loginUsername"),
				filterDynamic.getInitParameter("loginPassword"));
		filterDynamic.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
	}
}
