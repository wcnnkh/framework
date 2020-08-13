package scw.application;

import scw.beans.BeanFactory;
import scw.beans.xml.XmlBeanFactory;
import scw.core.utils.StringUtils;
import scw.logger.LoggerFactory;

public class CommonApplication extends XmlBeanFactory implements Application {
	public static final String DEFAULT_BEANS_PATH = "beans.xml";
	private volatile boolean start = false;

	public CommonApplication(String xmlConfigPath) {
		super(StringUtils.isEmpty(xmlConfigPath) ? DEFAULT_BEANS_PATH : xmlConfigPath);
	}

	public BeanFactory getBeanFactory() {
		return this;
	}

	public final synchronized void init() {
		if (start) {
			throw new ApplicationException("已经启动了");
		}

		start = true;

		try {
			super.init();
			initInternal();
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new ApplicationException("Initialization exception", e);
		}
	}

	protected void initInternal() throws Exception {
	}

	protected void destroyInternal() throws Exception {
	}

	public boolean isStart() {
		return start;
	}

	public final synchronized void destroy() {
		if (!start) {
			return;
		}

		start = false;
		try {
			try {
				destroyInternal();
			} finally {
				super.destroy();
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new ApplicationException("Destroy exception", e);
		} finally {
			LoggerFactory.getILoggerFactory().destroy();
		}
	}
}
