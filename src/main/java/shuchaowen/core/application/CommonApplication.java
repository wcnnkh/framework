package shuchaowen.core.application;

import java.util.Collection;

import shuchaowen.core.beans.AnnotationBeanFactory;
import shuchaowen.core.beans.BeanUtils;
import shuchaowen.core.beans.MultipleBeanFactory;
import shuchaowen.core.beans.xml.XmlBeanFactory;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.StringUtils;

public class CommonApplication implements Application {
	private MultipleBeanFactory beanFactory;
	private String packageNames;
	private volatile boolean start = false;

	public CommonApplication(String configPath) {
		beanFactory = new MultipleBeanFactory();
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
		
		try {
			BeanUtils.initStatic(beanFactory, getClasses());
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
		try {
			BeanUtils.destroyStaticMethod(getClasses());
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
}
