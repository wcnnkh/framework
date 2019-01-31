package scw.application;

import java.util.Collection;

import com.alibaba.dubbo.config.ProtocolConfig;

import scw.beans.XmlBeanFactory;
import scw.beans.property.PropertiesFactory;
import scw.beans.property.XmlPropertiesFactory;
import scw.beans.rpc.dubbo.XmlDubboUtils;
import scw.common.Logger;
import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;
import scw.database.DataBaseUtils;
import scw.database.TransactionContext;

public class CommonApplication implements Application {
	private static final String TRANSACTION_DEBUG_NAME = "shuchaowen.transaction.debug";
	private static final String TRANSACTION_CACHE_NAME = "shuchaowen.transaction.cache";
	private static final String PROXY_REGISTE_TABLE = "shuchaowen.proxy.register.table";

	private final XmlBeanFactory beanFactory;
	private volatile boolean start = false;
	private final PropertiesFactory propertiesFactory;
	private final String configPath;

	public CommonApplication(String configPath, boolean initStatic, PropertiesFactory propertiesFactory) {
		this.configPath = configPath;
		this.propertiesFactory = propertiesFactory == null ? new XmlPropertiesFactory(configPath) : propertiesFactory;
		try {
			this.beanFactory = new XmlBeanFactory(this.propertiesFactory, configPath, initStatic);
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public CommonApplication(String configXml, boolean initStatic) {
		this(configXml, initStatic, new XmlPropertiesFactory(configXml));
	}

	public Collection<Class<?>> getClasses() {
		return ClassUtils.getClasses(beanFactory.getPackages());
	}

	public XmlBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public PropertiesFactory getPropertiesFactory() {
		return beanFactory.getPropertiesFactory();
	}

	public void init() {
		if (start) {
			throw new ShuChaoWenRuntimeException("已经启动了");
		}

		synchronized (this) {
			if (start) {
				throw new ShuChaoWenRuntimeException("已经启动了");
			}

			start = true;
		}

		beanFactory.init();

		String transactionDebug = getPropertiesFactory().getValue(TRANSACTION_DEBUG_NAME);
		if (!StringUtils.isNull(transactionDebug)) {
			TransactionContext.getGlobaConfig().setDebug(Boolean.parseBoolean(transactionDebug));
		}

		String transactionCache = getPropertiesFactory().getValue(TRANSACTION_CACHE_NAME);
		if (!StringUtils.isNull(transactionCache)) {
			TransactionContext.getGlobaConfig().setSelectCache(Boolean.parseBoolean(transactionCache));
		}

		String registerTableBean = getPropertiesFactory().getValue(PROXY_REGISTE_TABLE);
		if (!StringUtils.isNull(registerTableBean)) {
			DataBaseUtils.registerCglibProxyTableBean(registerTableBean);
		}

		if (!StringUtils.isNull(configPath)) {
			new Thread(new Runnable() {

				public void run() {
					try {
						XmlDubboUtils.register(propertiesFactory, beanFactory, configPath);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	public void destroy() {
		if (!start) {
			throw new ShuChaoWenRuntimeException("还未启动，无法销毁");
		}

		synchronized (this) {
			if (!start) {
				throw new ShuChaoWenRuntimeException("还未启动，无法销毁");
			}

			start = false;
		}

		beanFactory.destroy();
		ProtocolConfig.destroyAll();
		Logger.shutdown();
	}
}
