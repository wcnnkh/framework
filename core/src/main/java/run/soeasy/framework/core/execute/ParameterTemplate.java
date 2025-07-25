package run.soeasy.framework.core.execute;

import run.soeasy.framework.core.transform.property.PropertyDescriptor;
import run.soeasy.framework.core.transform.property.PropertyTemplate;

/**
 * 参数模板接口，继承自{@link PropertyTemplate}，用于描述可执行元素（方法、构造函数等）的参数元数据，
 * 每个元素为{@link PropertyDescriptor}类型，代表单个参数的描述信息。
 * <p>
 * 该接口定义了参数集合的标准访问方式，支持按顺序访问参数描述符，
 * 是{@link ExecutableMetadata}等可执行元素元数据接口的核心组成部分。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>参数顺序维护：保持参数在可执行元素中的声明顺序</li>
 *   <li>类型安全访问：通过泛型约束确保元素为{@link PropertyDescriptor}</li>
 *   <li>可执行元素关联：作为可执行元素元数据的参数描述部分</li>
 *   <li>继承基础功能：继承{@link PropertyTemplate}的所有属性访问能力</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>元素类型为{@link PropertyDescriptor}，每个元素描述一个参数的元数据</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>反射调用：获取方法或构造函数的参数信息以准备调用</li>
 *   <li>参数校验：根据参数模板验证传入参数的合法性</li>
 *   <li>动态代理：生成代理时获取原始方法的参数结构</li>
 *   <li>文档生成：基于参数模板自动生成API文档</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PropertyTemplate
 * @see PropertyDescriptor
 * @see ExecutableMetadata
 */
public interface ParameterTemplate extends PropertyTemplate<PropertyDescriptor> {
}