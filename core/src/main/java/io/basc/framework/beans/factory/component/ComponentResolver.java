package io.basc.framework.beans.factory.component;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.core.type.AnnotatedTypeMetadata;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.MethodMetadata;
import io.basc.framework.env.EnvironmentCapable;
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
	 * @param annotatedTypeMetadata
	 * @return
	 */
	boolean isComponent(AnnotatedTypeMetadata annotatedTypeMetadata);

	/**
	 * 获取别名
	 * 
	 * @param beanDefinition
	 * @return
	 */
	Elements<String> getAliasNames(BeanDefinition beanDefinition);

	BeanDefinition createComponent(AnnotationMetadata componentAnnotationMetadata, ClassLoader classLoader);

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
	 * @param methodMetadata
	 * @return
	 */
	BeanDefinition createComponent(BeanDefinition component, MethodMetadata methodMetadata);
	
	boolean matchs(EnvironmentCapable context, BeanDefinitionRegistry registry, AnnotatedTypeMetadata annotatedTypeMetadata);
}
