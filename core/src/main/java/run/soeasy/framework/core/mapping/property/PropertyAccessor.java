package run.soeasy.framework.core.mapping.property;

import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * 属性访问器接口，继承自{@link PropertyDescriptor}和{@link TypedValueAccessor}，
 * 整合属性元数据描述与类型化值访问能力，是属性操作的核心接口。
 * <p>
 * 该接口融合了两大核心能力：
 * <ul>
 *   <li>属性描述：继承{@link PropertyDescriptor}的属性名称、读写状态等元数据</li>
 *   <li>值操作：继承{@link TypedValueAccessor}的类型化值访问能力</li>
 * </ul>
 * 适用于需要同时获取属性描述信息和执行类型化值操作的场景，如对象映射、数据绑定等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>元数据访问：获取属性名称、读写状态等描述信息</li>
 *   <li>类型化操作：支持带类型信息的值读取和写入</li>
 *   <li>接口整合：统一属性描述与值操作的访问入口</li>
 *   <li>空安全设计：规范null值场景下的操作行为</li>
 * </ul>
 *
 * <p><b>实现注意事项：</b>
 * <ul>
 *   <li>类型一致性：确保属性描述与值操作的类型信息一致</li>
 *   <li>线程安全：若涉及共享对象访问，需保证操作的线程安全性</li>
 *   <li>异常规范：遵循{@link TypedValueAccessor}的异常处理规范</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PropertyDescriptor
 * @see TypedValueAccessor
 * @see ObjectPropertyAccessor
 */
public interface PropertyAccessor extends PropertyDescriptor, TypedValueAccessor {
    // 接口仅作为能力组合，无额外方法定义
}