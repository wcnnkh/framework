package shuchaowen.beans;

import shuchaowen.beans.annotaion.Service;
import shuchaowen.beans.property.PropertiesFactory;
import shuchaowen.common.exception.AlreadyExistsException;
import shuchaowen.common.utils.ClassUtils;

/**
 * 此类只要类是存在的不可能出现获取不到的情况
 * 
 * @author shuchaowen
 *
 */
public class AnnotationBeanFactory extends AbstractBeanFactory {
	private final BeanFactory beanFactory;
	private final PropertiesFactory propertiesFactory;
	
	public AnnotationBeanFactory(BeanFactory beanFactory, PropertiesFactory propertiesFactory, String packageNames) {
		this.beanFactory = beanFactory;
		this.propertiesFactory = propertiesFactory;
		for (Class<?> clz : ClassUtils.getClasses(packageNames)) {
			Service service = clz.getAnnotation(Service.class);
			if (service != null) {
				Class<?>[] interfaces = clz.getInterfaces();
				for (Class<?> i : interfaces) {
					if(!registerNameMapping(i.getName(), clz.getName())){
						throw new AlreadyExistsException(i.getName());
					}
				}

				if (!service.value().equals("")) {
					if(!registerNameMapping(service.value(), clz.getName())){
						throw new AlreadyExistsException(service.value());
					}
				}
			}
		}
	}

	@Override
	protected Bean newBean(String name) {
		try {
			Class<?> clz = Class.forName(name);
			if(!ClassUtils.isInstance(clz)){
				return  null;
			}
			
			return new AnnotationBean(beanFactory, propertiesFactory, clz);
		} catch (Exception e) {
		}
		return null;
	}
	
	@Override
	public boolean contains(String name) {
		boolean b = super.contains(name);
		if(!b){
			try {
				Class<?> clz = ClassUtils.forName(name);
				if(ClassUtils.isInstance(clz)){
					b = true;
				}
			} catch (ClassNotFoundException e) {
			}
		}
		return b;
	}
}
