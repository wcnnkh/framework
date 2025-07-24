package run.soeasy.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.transform.property.MapPropertyTemplate;
import run.soeasy.framework.core.transform.property.PropertyTemplate;
import run.soeasy.framework.core.type.ReflectionUtils;

/**
 * 基于JavaBean的属性模板实现，继承自{@link MapPropertyTemplate}，实现{@link PropertyTemplate<BeanProperty>}接口，
 * 用于管理和访问JavaBean的属性信息（封装为{@link BeanProperty}），支持通过JavaBean内省机制获取属性元数据，
 * 并过滤掉无需处理的基础属性（如Object类的方法对应的属性）。
 * 
 * <p>该类通过{@link BeanInfoFactory}获取JavaBean的{@link BeanInfo}，将属性描述符转换为{@link BeanProperty}对象，
 * 构建属性模板集合，适用于属性映射、数据转换、反射操作等场景。
 * 
 * @author soeasy.run
 * @see MapPropertyTemplate
 * @see PropertyTemplate
 * @see BeanProperty
 * @see BeanInfo
 */
class BeanPropertyTemplate extends MapPropertyTemplate<BeanProperty, PropertyTemplate<BeanProperty>>
		implements PropertyTemplate<BeanProperty> {

    /**
     * 构造Bean属性模板（基于指定的Bean类和Bean信息工厂）
     * 
     * <p>通过{@link BeanInfoFactory}获取目标Bean类的{@link BeanInfo}，解析其中的属性描述符并转换为{@link BeanProperty}，
     * 同时过滤掉需要忽略的属性（如Object类自带方法对应的属性），完成属性模板的初始化。
     * 
     * @param beanClass 目标JavaBean的类对象（非空）
     * @param beanInfoFactory 用于获取Bean信息的工厂（非空）
     * @throws FatalBeanException 当获取BeanInfo失败时抛出（包装{@link IntrospectionException}）
     */
	public BeanPropertyTemplate(@NonNull Class<?> beanClass, BeanInfoFactory beanInfoFactory) {
		super(() -> {
			BeanInfo beanInfo;
			try {
				beanInfo = beanInfoFactory.getBeanInfo(beanClass);
			} catch (IntrospectionException e) {
				throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass + "]", e);
			}
			// 将属性描述符转换为BeanProperty，并过滤忽略的属性
			return Stream.of(beanInfo.getPropertyDescriptors())
					.map((descriptor) -> new BeanProperty(beanClass, descriptor))
					.filter((property) -> !isIgnoreProperty(property))
					.iterator();
		}, false);
	}

    /**
     * 判断是否为需要忽略的属性
     * 
     * <p>忽略规则：若属性的读方法（getter）是Object类中定义的方法（如{@link Object#getClass()}），则视为需要忽略的属性，
     * 避免处理JavaBean继承自Object的基础属性。
     * 
     * @param property 待判断的Bean属性
     * @return 是需要忽略的属性则返回true，否则返回false
     */
	private static boolean isIgnoreProperty(BeanProperty property) {
		if (property.getReadMethod() != null && property.getReadMethod().getSource() != null
				&& ReflectionUtils.isObjectMethod(property.getReadMethod().getSource())) {
			return true;
		}
		return false;
	}
}