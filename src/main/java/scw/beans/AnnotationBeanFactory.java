package scw.beans;

import scw.beans.annotaion.Service;
import scw.beans.property.PropertiesFactory;
import scw.common.exception.AlreadyExistsException;
import scw.common.utils.ClassUtils;

/**
 * 此类只要类是存在的不可能出现获取不到的情况
 * 
 * @author shuchaowen
 *
 */
public class AnnotationBeanFactory extends AbstractBeanFactory {
	public AnnotationBeanFactory(BeanFactory beanFactory, PropertiesFactory propertiesFactory, String packageNames,
			String[] filterNames) throws Exception {
		for (Class<?> clz : ClassUtils.getClasses(packageNames)) {
			Service service = clz.getAnnotation(Service.class);
			if (service != null) {
				AnnotationBean bean = new AnnotationBean(beanFactory, propertiesFactory, clz, filterNames);
				putBean(clz.getName(), bean);

				Class<?>[] interfaces = clz.getInterfaces();
				for (Class<?> i : interfaces) {
					if (!registerNameMapping(i.getName(), clz.getName())) {
						throw new AlreadyExistsException(i.getName());
					}
				}

				if (!service.value().equals("")) {
					if (!registerNameMapping(service.value(), clz.getName())) {
						throw new AlreadyExistsException(service.value());
					}
				}
			}
		}
	}

}
