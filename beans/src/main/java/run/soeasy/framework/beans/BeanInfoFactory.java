package run.soeasy.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import lombok.NonNull;
import run.soeasy.framework.core.mapping.property.ObjectTemplateFactory;
import run.soeasy.framework.core.mapping.property.PropertyMapping;

/**
 * Bean信息工厂接口，继承自{@link ObjectTemplateFactory}，定义获取JavaBean信息（{@link BeanInfo}）和创建Bean属性模板的规范，
 * 作为JavaBean内省机制的抽象，提供Bean信息的统一获取方式，适配不同的Bean信息解析策略。
 * 
 * <p>
 * 该接口结合内省机制，既可以直接获取{@link BeanInfo}用于自定义处理，也可以通过默认方法创建{@link BeanPropertyTemplate}，
 * 简化Bean属性元数据的访问与管理流程。
 * 
 * @author soeasy.run
 * @see ObjectTemplateFactory
 * @see BeanInfo
 * @see BeanPropertyTemplate
 */
public interface BeanInfoFactory extends ObjectTemplateFactory<BeanProperty> {

	/**
	 * 获取指定Bean类的{@link BeanInfo}（JavaBean信息）
	 * 
	 * <p>
	 * 通过内省机制解析Bean类，获取包含属性、方法、事件等信息的{@link BeanInfo}对象，
	 * 是后续创建{@link BeanProperty}和{@link BeanPropertyTemplate}的基础。
	 * 
	 * @param beanClass 目标Bean类（非空）
	 * @return 包含Bean元信息的{@link BeanInfo}对象
	 * @throws IntrospectionException 当内省过程失败时抛出（如Bean类无法被解析）
	 */
	BeanInfo getBeanInfo(@NonNull Class<?> beanClass) throws IntrospectionException;

	/**
	 * 创建指定Bean类的属性模板（默认实现）
	 * 
	 * <p>
	 * 基于当前{@link BeanInfoFactory}实例和目标Bean类，创建{@link BeanPropertyTemplate}，
	 * 实现{@link ObjectTemplateFactory}接口的规范方法，提供Bean属性模板的默认创建逻辑。
	 * 
	 * @param objectClass 目标Bean类（即JavaBean的类对象）
	 * @return 针对该Bean类的{@link BeanPropertyTemplate}实例
	 */
	@Override
	default PropertyMapping<BeanProperty> getObjectTemplate(Class<?> objectClass) {
		return new BeanInfoMapping(objectClass, this).standard();
	}
}