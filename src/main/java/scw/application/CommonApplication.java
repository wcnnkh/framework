package scw.application;

import java.util.Collection;

import com.alibaba.dubbo.config.ProtocolConfig;

import scw.beans.AnnotationBeanFactory;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.MultipleBeanFactory;
import scw.beans.property.PropertiesFactory;
import scw.beans.property.XmlPropertiesFactory;
import scw.beans.rpc.dubbo.XmlDubboBeanFactory;
import scw.beans.rpc.dubbo.XmlDubboUtils;
import scw.beans.rpc.http.HttpRPCBeanFactory;
import scw.beans.xml.XmlBeanFactory;
import scw.common.Logger;
import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;

public class CommonApplication implements Application {
	private final MultipleBeanFactory beanFactory;
	private final boolean initStatic;
	private String packageNames;
	private volatile boolean start = false;
	private PropertiesFactory propertiesFactory;
	private final String configPath;

	public CommonApplication(String configPath, boolean initStatic) {
		beanFactory = new MultipleBeanFactory();
		this.configPath = configPath;
		this.initStatic = initStatic;
		try {
			propertiesFactory = new XmlPropertiesFactory(configPath);
			if (!StringUtils.isNull(configPath)) {
				BeanFactory dubboBeanFactory = new XmlDubboBeanFactory(propertiesFactory, configPath);
				beanFactory.addLastBeanFactory(dubboBeanFactory);

				HttpRPCBeanFactory scwrpcBeanFactory = new HttpRPCBeanFactory(propertiesFactory, configPath);
				beanFactory.addLastBeanFactory(scwrpcBeanFactory);

				XmlBeanFactory xmlBeanFactory = new XmlBeanFactory(beanFactory, propertiesFactory, configPath);
				this.packageNames = xmlBeanFactory.getPackageNames();
				beanFactory.addLastBeanFactory(xmlBeanFactory);
			}

			AnnotationBeanFactory annotationBeanFactory = new AnnotationBeanFactory(beanFactory, propertiesFactory,
					packageNames);
			beanFactory.addLastBeanFactory(annotationBeanFactory);
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public Collection<Class<?>> getClasses() {
		return ClassUtils.getClasses(packageNames);
	}

	public MultipleBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public PropertiesFactory getPropertiesFactory() {
		return propertiesFactory;
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

		try {
			if (initStatic) {
				BeanUtils.initStatic(beanFactory, propertiesFactory, getClasses());
			}
			if (!StringUtils.isNull(configPath)) {
				XmlDubboUtils.register(propertiesFactory, beanFactory, configPath);
			}
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
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

		if (initStatic) {
			try {
				BeanUtils.destroyStaticMethod(getClasses());
			} catch (Exception e) {
				throw new ShuChaoWenRuntimeException(e);
			}
		}

		ProtocolConfig.destroyAll();
		Logger.shutdown();
	}
}
