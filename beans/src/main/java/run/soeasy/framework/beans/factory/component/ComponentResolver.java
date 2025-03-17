package run.soeasy.framework.beans.factory.component;

import run.soeasy.framework.beans.factory.config.BeanDefinition;
import run.soeasy.framework.core.type.AnnotatedTypeMetadata;
import run.soeasy.framework.core.type.AnnotationMetadata;
import run.soeasy.framework.core.type.MethodMetadata;
import run.soeasy.framework.util.collections.Elements;

/**
 * 组件解析
 * 
 * @author shuchaowen
 *
 */
public interface ComponentResolver extends ComponentCondition{
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
}
