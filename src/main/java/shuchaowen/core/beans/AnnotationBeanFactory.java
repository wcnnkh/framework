package shuchaowen.core.beans;

import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.util.ClassUtils;

/**
 * 此类只要类是存在的不可能出现获取不到的情况
 * 
 * @author shuchaowen
 *
 */
public class AnnotationBeanFactory extends AbstractBeanFactory {
	private BeanFactory beanFactory;
	
	public AnnotationBeanFactory(BeanFactory beanFactory, String packageNames) {
		this.beanFactory = beanFactory;
		for (Class<?> clz : ClassUtils.getClasses(packageNames)) {
			Service service = clz.getAnnotation(Service.class);
			if (service != null) {
				Class<?>[] interfaces = clz.getInterfaces();
				for (Class<?> i : interfaces) {
					putNameMapping(i.getName(), clz.getName());
				}

				if (!service.value().equals("")) {
					putNameMapping(service.value(), clz.getName());
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
			
			return new AnnotationBean(beanFactory, clz);
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
