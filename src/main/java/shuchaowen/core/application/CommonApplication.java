package shuchaowen.core.application;

import java.util.Collection;

import shuchaowen.core.beans.AnnotationBeanFactory;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.BeanUtils;
import shuchaowen.core.beans.MultipleBeanFactory;
import shuchaowen.core.beans.property.PropertiesFactory;
import shuchaowen.core.beans.property.XmlPropertiesFactory;
import shuchaowen.core.beans.rpc.dubbo.XmlDubboBeanFactory;
import shuchaowen.core.beans.rpc.dubbo.XmlDubboUtils;
import shuchaowen.core.beans.rpc.http.SCWRPCBeanFactory;
import shuchaowen.core.beans.xml.XmlBeanFactory;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.Logger;
import shuchaowen.core.util.StringUtils;

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
				
				SCWRPCBeanFactory scwrpcBeanFactory = new SCWRPCBeanFactory(propertiesFactory, configPath);
				beanFactory.addLastBeanFactory(scwrpcBeanFactory);
				
				XmlBeanFactory xmlBeanFactory = new XmlBeanFactory(beanFactory, propertiesFactory, configPath);
				this.packageNames = xmlBeanFactory.getPackageNames();
				beanFactory.addLastBeanFactory(xmlBeanFactory);
			}
			
			AnnotationBeanFactory annotationBeanFactory = new AnnotationBeanFactory(beanFactory, propertiesFactory, packageNames);
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
			if(initStatic){
				BeanUtils.initStatic(beanFactory, getClasses());
			}
			
			if(!StringUtils.isNull(configPath)){
				XmlDubboUtils.register(propertiesFactory, beanFactory, configPath);
			}	
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public void destroy() {
		if(!start){
			throw new ShuChaoWenRuntimeException("还未启动，无法销毁");
		}
		
		synchronized (this) {
			if (!start) {
				throw new ShuChaoWenRuntimeException("还未启动，无法销毁");
			}
			
			start = false;
		}
		
		beanFactory.destroy();
		
		if(initStatic){
			try {
				BeanUtils.destroyStaticMethod(getClasses());
			} catch (Exception e) {
				throw new ShuChaoWenRuntimeException(e);
			}
		}
		Logger.shutdown();
	}
}
