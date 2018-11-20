package shuchaowen.core.application;

import java.util.Collection;

import shuchaowen.core.beans.AnnotationBeanFactory;
import shuchaowen.core.beans.BeanUtils;
import shuchaowen.core.beans.MultipleBeanFactory;
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

	public CommonApplication(String configPath, boolean initStatic) {
		beanFactory = new MultipleBeanFactory();
		this.initStatic = initStatic;
		try {
			if (!StringUtils.isNull(configPath)) {
				XmlBeanFactory xmlBeanFactory = new XmlBeanFactory(beanFactory, configPath);
				this.packageNames = xmlBeanFactory.getPackageNames();
				beanFactory.addLastBeanFactory(xmlBeanFactory);
			}
			
			AnnotationBeanFactory annotationBeanFactory = new AnnotationBeanFactory(beanFactory, packageNames);
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
		
		if(initStatic){
			try {
				BeanUtils.initStatic(beanFactory, getClasses());
			} catch (Exception e) {
				throw new ShuChaoWenRuntimeException(e);
			}
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
