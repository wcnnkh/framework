package io.basc.framework.beans.factory.annotation;

import java.lang.reflect.Method;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.element.Elements;

/**
 * 组件解析
 * 
 * @author shuchaowen
 *
 */
public interface ComponentResolver {
	/**
	 * 是否是一个组件
	 * 
	 * @param typeDescriptor
	 * @return
	 */
	boolean isComponent(TypeDescriptor typeDescriptor);

	/**
	 * 获取别名
	 * 
	 * @param beanDefinition
	 * @return
	 */
	Elements<String> getAliasNames(BeanDefinition beanDefinition);

	BeanDefinition createComponent(Class<?> componentClass);

	/**
	 * 是否是配置组件
	 * 
	 * @param component
	 * @return
	 */
	boolean isConfiguration(BeanDefinition component);

	/**
	 * 从组件类方法中创建bean的定义
	 * 
	 * @param component
	 * @param method
	 * @return
	 */
	BeanDefinition createBeanDefinition(BeanDefinition component, Method method);
}
