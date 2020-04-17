package scw.embed.servlet;

import scw.application.MainApplication;
import scw.core.instance.InstanceUtils;
import scw.core.instance.annotation.Configuration;
import scw.lang.UnsupportedException;
import scw.servlet.mvc.DispatcherServlet;
import scw.util.FormatUtils;

@Configuration(MainApplication.class)
public class ServletEmbeddedApplication extends MainApplication {
	private ServletEmbedded embedded;

	public ServletEmbeddedApplication(Class<?> mainClass, String[] args) {
		super(mainClass, args);
	}

	@Override
	protected void initInternal() {
		super.initInternal();
		embedded = InstanceUtils.getConfiguration(ServletEmbedded.class,
				getBeanFactory(), getPropertyFactory());
		if (embedded == null) {
			throw new UnsupportedException("未找到支持的embedded, 如需支持请导入对应的jar");
		}

		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		dispatcherServlet.setCommonApplication(this);
		if (propertyFactory.getValue("servlet.service.startup", boolean.class,
				true)) {
			dispatcherServlet.setDefaultServletService(false);
		}
		embedded.init(getBeanFactory(), getPropertyFactory(),
				new ShutdownHttpServlet(getPropertyFactory(), this),
				dispatcherServlet, getMainClass());
	}

	@Override
	public void destroyInternal() {
		FormatUtils.info(ServletEmbeddedApplication.class,
				"---------------shutdown---------------");
		if (embedded != null) {
			embedded.destroy();
		}
		super.destroy();
		System.exit(0);
	}
}
