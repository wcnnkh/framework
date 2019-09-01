package scw.beans.auto;

import java.util.Arrays;

import scw.beans.AnnotationBeanDefinition;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.NoArgumentBeanMethod;
import scw.beans.property.ValueWiredManager;
import scw.core.Init;
import scw.core.PropertyFactory;
import scw.core.exception.BeansException;
import scw.core.reflect.FieldDefinition;

public class AutoBeanDefinition implements BeanDefinition {
	private final BeanFactory beanFactory;
	private final Class<?> type;
	private final String id;
	private NoArgumentBeanMethod[] initMethods;
	private  NoArgumentBeanMethod[] destroyMethods;
	private final PropertyFactory propertyFactory;
	private final boolean singleton;
	private FieldDefinition[] autowriteFieldDefinition;
	private final String[] names;
	private final ValueWiredManager valueWiredManager;
	private final AutoBean autoBean;
	private final AutoBeanConfig autoBeanConfig;

	public AutoBeanDefinition(ValueWiredManager valueWiredManager,
			BeanFactory beanFactory, PropertyFactory propertyFactory,
			Class<?> type, String[] filterNames, AutoBean autoBean)
			throws Exception {
		this.valueWiredManager = valueWiredManager;
		this.beanFactory = beanFactory;
		this.type = type;
		this.propertyFactory = propertyFactory;
		this.id = type.getName();
		this.names = AnnotationBeanDefinition.getServiceNames(type);
		this.autoBean = autoBean;
		
		if(autoBean.initEnable()){
			this.initMethods = AnnotationBeanDefinition.getInitMethodList(type)
					.toArray(new NoArgumentBeanMethod[0]);
		}
		
		if(autoBean.destroyEnable()){
			this.destroyMethods = AnnotationBeanDefinition.getDestroyMethdoList(
					type).toArray(new NoArgumentBeanMethod[0]);
		}
		
		scw.beans.annotation.Bean bean = type
				.getAnnotation(scw.beans.annotation.Bean.class);
		this.singleton = bean == null ? true : bean.singleton();
		
		if(autoBean.autowriedEnable()){
			this.autowriteFieldDefinition = BeanUtils
					.getAutowriteFieldDefinitionList(type, false).toArray(
							new FieldDefinition[0]);
		}
		
		this.autoBeanConfig = new SimpleAutoBeanConfig(filterNames);
	}

	public boolean isSingleton() {
		return singleton;
	}

	public String getId() {
		return this.id;
	}

	public Class<?> getType() {
		return this.type;
	}

	public void autowrite(Object bean) throws Exception {
		if(autowriteFieldDefinition != null){
			BeanUtils.autoWrite(valueWiredManager, beanFactory, propertyFactory,
					type, bean, Arrays.asList(autowriteFieldDefinition));
		}
	}

	public void init(Object bean) throws Exception {
		if(!autoBean.initEnable()){
			return ;
		}
		if (initMethods != null && initMethods.length != 0) {
			for (NoArgumentBeanMethod method : initMethods) {
				method.noArgumentInvoke(bean);
			}
		}

		if (bean instanceof Init) {
			((Init) bean).init();
		}
	}

	public void destroy(Object bean) throws Exception {
		if(!autoBean.destroyEnable()){
			return ;
		}
		
		valueWiredManager.cancel(bean);
		if (destroyMethods != null && destroyMethods.length != 0) {
			for (NoArgumentBeanMethod method : destroyMethods) {
				method.invoke(bean);
			}
		}

		if (bean instanceof scw.core.Destroy) {
			((scw.core.Destroy) bean).destroy();
		}
	}

	public String[] getNames() {
		return names;
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		try {
			return (T) autoBean.create(autoBeanConfig);
		} catch (Exception e) {
			throw new BeansException(getId(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Object... params) {
		try {
			return (T) autoBean.create(autoBeanConfig, params);
		} catch (Exception e) {
			throw new BeansException(getId(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<?>[] parameterTypes, Object... params) {
		try {
			return (T) autoBean.create(autoBeanConfig, parameterTypes, params);
		} catch (Exception e) {
			throw new BeansException(getId(), e);
		}
	}
}
