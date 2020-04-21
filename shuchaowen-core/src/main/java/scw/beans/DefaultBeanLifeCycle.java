package scw.beans;

import java.lang.reflect.Modifier;

import scw.aop.ProxyUtils;
import scw.beans.annotation.Autowired;
import scw.beans.annotation.Bean;
import scw.beans.annotation.Config;
import scw.beans.annotation.Value;
import scw.core.instance.annotation.Configuration;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.XUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE)
@Bean(proxy = false)
public class DefaultBeanLifeCycle implements BeanLifeCycle {
	protected final Logger logger = LoggerUtils.getLogger(getClass());

	protected BeanMetadata getBeanMetadata(Class<?> targetClass) {
		return new BeanMetadata(targetClass);
	}

	public void initBefore(BeanFactory beanFactory,
			PropertyFactory propertyFactory, BeanDefinition definition,
			Object instance) throws Exception {
		if (instance instanceof BeanFactoryAccessor) {
			((BeanFactoryAccessor) instance).setBeanFactory(beanFactory);
		}

		if (instance instanceof BeanDefinitionAware) {
			((BeanDefinitionAware) instance).setBeanDefinition(definition);
		}

		Class<?> instanceClass = ProxyUtils.getProxyAdapter().getUserClass(
				instance.getClass());
		for (FieldDefinition fieldDefinition : getBeanMetadata(instanceClass)
				.getAutowriteFieldDefinitions()) {
			setConfig(beanFactory, propertyFactory, instanceClass, instance,
					fieldDefinition);
			setValue(beanFactory, propertyFactory, instanceClass, instance,
					fieldDefinition);
			setBean(beanFactory, propertyFactory, instanceClass, instance,
					fieldDefinition);
		}
	}

	public void initAfter(BeanFactory beanFactory,
			PropertyFactory propertyFactory, BeanDefinition definition,
			Object instance) throws Exception {
		Class<?> instanceClass = ProxyUtils.getProxyAdapter().getUserClass(
				instance.getClass());
		for (BeanMethod beanMethod : getBeanMetadata(instanceClass)
				.getInitMethods()) {
			beanMethod.invoke(instance, beanFactory, propertyFactory);
		}
		XUtils.init(instance);
	}

	public void destroyBefore(BeanFactory beanFactory,
			PropertyFactory propertyFactory, BeanDefinition definition,
			Object instance) throws Exception {
	}

	public void destroyAfter(BeanFactory beanFactory,
			PropertyFactory propertyFactory, BeanDefinition definition,
			Object instance) throws Exception {
		Class<?> instanceClass = ProxyUtils.getProxyAdapter().getUserClass(
				instance.getClass());
		for (BeanMethod beanMethod : getBeanMetadata(instanceClass)
				.getDestroyMethods()) {
			beanMethod.invoke(instance, beanFactory, propertyFactory);
		}
		XUtils.destroy(instance);
	}

	protected void existDefaultValueWarnLog(String tag, Class<?> clz,
			FieldDefinition field, Object obj) throws Exception {
		if (checkExistDefaultValue(field, obj)) {
			logger.warn("{} class[{}] fieldName[{}] existence default value",
					tag, clz.getName(), field.getField().getName());
		}
	}

	protected void staticFieldWarnLog(String tag, Class<?> clz,
			FieldDefinition field) {
		if (Modifier.isStatic(field.getField().getModifiers())) {
			logger.warn("{} class[{}] fieldName[{}] is a static field", tag,
					clz.getName(), field.getField().getName());
		}
	}

	protected boolean checkExistDefaultValue(FieldDefinition field, Object obj)
			throws Exception {
		if (field.getField().getType().isPrimitive()) {// 值类型一定是有默认值的,所以不用判断直接所回false
			return false;
		}
		return field.get(obj) != null;
	}

	protected void setBean(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> clz, Object obj,
			FieldDefinition field) {
		Autowired s = field.getAnnotatedElement()
				.getAnnotation(Autowired.class);
		if (s != null) {
			staticFieldWarnLog(Autowired.class.getName(), clz, field);

			String name = s.value();
			if (name.length() == 0) {
				name = field.getField().getType().getName();
			}

			try {
				existDefaultValueWarnLog(Autowired.class.getName(), clz, field,
						obj);
				field.set(obj, beanFactory.getInstance(name));
			} catch (Exception e) {
				throw new RuntimeException("autowrite：clz=" + clz.getName()
						+ ",fieldName=" + field.getField().getName(), e);
			}
		}
	}

	protected void setConfig(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> clz, Object obj,
			FieldDefinition field) {
		Config config = field.getAnnotatedElement().getAnnotation(Config.class);
		if (config != null) {
			staticFieldWarnLog(Config.class.getName(), clz, field);
			Object value = null;
			try {
				existDefaultValueWarnLog(Config.class.getName(), clz, field,
						obj);

				value = beanFactory.getInstance(config.parse()).parse(
						beanFactory, propertyFactory, field, config.value(),
						config.charset());
				field.set(obj, value);
			} catch (Exception e) {
				throw new RuntimeException("config：clz=" + clz.getName()
						+ ",fieldName=" + field.getField().getName(), e);
			}
		}
	}

	protected void setValue(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> clz, Object obj,
			FieldDefinition field) throws Exception {
		Value value = field.getAnnotatedElement().getAnnotation(Value.class);
		if (value != null) {
			staticFieldWarnLog(Value.class.getName(), clz, field);
			try {
				existDefaultValueWarnLog(Value.class.getName(), clz, field, obj);
				Object v = beanFactory.getInstance(value.format()).format(
						beanFactory, propertyFactory, field, value.value());
				if (v != null) {
					field.set(obj, v);
				}
			} catch (Throwable e) {
				throw new RuntimeException("value：clz=" + clz.getName()
						+ ",fieldName=" + field.getField().getName(), e);
			}
		}
	}
}
