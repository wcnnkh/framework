package scw.embed.servlet;

import scw.application.MainApplication;
import scw.application.MainArgs;
import scw.core.instance.InstanceUtils;
import scw.core.instance.annotation.Configuration;
import scw.lang.NotSupportedException;
import scw.logger.SplitLineAppend;
import scw.servlet.DispatcherServlet;
import scw.servlet.http.HttpServletService;

@Configuration(MainApplication.class)
public class ServletEmbeddedApplication extends MainApplication {
	private ServletEmbedded embedded;

	public ServletEmbeddedApplication(Class<?> mainClass, MainArgs args) {
		super(mainClass, args);
	}

	@Override
	protected void initInternal() {
		super.initInternal();
		embedded = InstanceUtils.loadService(ServletEmbedded.class, getBeanFactory(), getPropertyFactory(), "scw.embed.tomcat.TomcatServletEmbedded");
		if (embedded == null) {
			throw new NotSupportedException("未找到支持的embedded, 如需支持请导入对应的jar");
		}

		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		dispatcherServlet.setApplication(this);
		if (propertyFactory.getValue("http.servlet.service.startup", boolean.class, true)) {
			dispatcherServlet.setHttpServletService(getBeanFactory().getInstance(HttpServletService.class));
		}
		embedded.init(getBeanFactory(), getPropertyFactory(), new ShutdownHttpServlet(getPropertyFactory(), this),
				dispatcherServlet, getMainClass(), getArgs());
	}

	@Override
	protected void destroyInternal() {
		getLogger().info(new SplitLineAppend("shutdown"));
		if (embedded != null) {
			embedded.destroy();
		}
		super.destroyInternal();
	}
}
