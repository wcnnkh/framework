package scw.beans;

import scw.beans.annotaion.Service;
import scw.beans.property.PropertiesFactory;
import scw.common.utils.ClassUtils;

/**
 * 此类只要类是存在的不可能出现获取不到的情况
 * 
 * @author shuchaowen
 *
 */
public class ServiceBeanConfigFactory extends AbstractBeanConfigFactory {
	public ServiceBeanConfigFactory(BeanFactory beanFactory, PropertiesFactory propertiesFactory, String packageNames,
			String[] filterNames) throws Exception {
		for (Class<?> clz : ClassUtils.getClasses(packageNames)) {
			Service service = clz.getAnnotation(Service.class);
			if (service != null) {
				AnnotationBean bean = new AnnotationBean(beanFactory, propertiesFactory, clz, filterNames);
				putBean(clz.getName(), bean);
			}
		}
	}

}
