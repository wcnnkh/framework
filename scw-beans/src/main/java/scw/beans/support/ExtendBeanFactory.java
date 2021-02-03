package scw.beans.support;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.SingletonBeanRegistry;
import scw.context.Destroy;
import scw.core.parameter.ParameterDescriptors;
import scw.core.parameter.ParameterFactory;
import scw.instance.InstanceException;
import scw.instance.NoArgsInstanceFactory;
import scw.util.Creator;
import scw.util.Result;

public class ExtendBeanFactory implements NoArgsInstanceFactory, Destroy {
	private final ParameterFactory parameterFactory;
	private final BeanFactory beanFactory;
	private final SingletonBeanRegistry singletonBeanRegistry;

	public ExtendBeanFactory(ParameterFactory parameterFactory, BeanFactory beanFactory) {
		this.parameterFactory = parameterFactory;
		this.beanFactory = beanFactory;
		this.singletonBeanRegistry = new DefaultSingletonBeanRegistry(beanFactory);
	}

	public <T> T getInstance(Class<T> clazz) {
		return getInstance(clazz.getName());
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		Object instance = singletonBeanRegistry.getSingleton(name);
		if (instance != null) {
			return (T) instance;
		}
		
		final BeanDefinition beanDefinition = beanFactory.getDefinition(name);
		if (beanDefinition == null) {
			return null;
		}
		
		instance = singletonBeanRegistry.getSingleton(beanDefinition.getId());
		if(instance != null){
			return (T) instance;
		}
		
		if(beanDefinition.isSingleton() && beanDefinition.isInstance()){
			return beanFactory.getInstance(name);
		}

		Result<Object> result = null;
		for (final ParameterDescriptors parameterDescriptors : beanDefinition) {
			if (parameterFactory.isAccept(parameterDescriptors)) {
				if (beanDefinition.isSingleton()) {
					result = singletonBeanRegistry.getSingleton(beanDefinition.getId(), new Creator<Object>() {

						public Object create() throws InstanceException {
							return beanDefinition.create(parameterDescriptors.getTypes(),
									parameterFactory.getParameters(parameterDescriptors));
						}
					});
				}else{
					result = new Result<Object>(true, beanDefinition.create(parameterDescriptors.getTypes(),
									parameterFactory.getParameters(parameterDescriptors)));
				}
			}
		}
		
		if(result != null){
			Object obj = result.getResult();
			if(result.isActive()){
				beanDefinition.dependence(obj);
				beanDefinition.init(obj);
			}
			return (T) obj;
		}
		return beanFactory.getInstance(name);
	}

	public boolean isInstance(String name) {
		if(singletonBeanRegistry.containsSingleton(name)){
			return true;
		}
		
		BeanDefinition beanDefinition = beanFactory.getDefinition(name);
		if (beanDefinition == null) {
			return false;
		}
		
		if(singletonBeanRegistry.containsSingleton(beanDefinition.getId())){
			return true;
		}
		
		if(beanDefinition.isSingleton() && beanDefinition.isInstance()){
			return true;
		}

		for (ParameterDescriptors parameterDescriptors : beanDefinition) {
			if (parameterFactory.isAccept(parameterDescriptors)) {
				return true;
			}
		}
		return false;
	}

	public boolean isInstance(Class<?> clazz) {
		return isInstance(clazz.getName());
	}

	public boolean isSingleton(String name) {
		return beanFactory.isInstance(name);
	}

	public boolean isSingleton(Class<?> clazz) {
		return isInstance(clazz.getName());
	}

	public ClassLoader getClassLoader() {
		return beanFactory.getClassLoader();
	}

	public void destroy() {
		singletonBeanRegistry.destroyAll();
	}
}
