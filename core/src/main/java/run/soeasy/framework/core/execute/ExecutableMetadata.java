package run.soeasy.framework.core.execute;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyMapping;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 可执行元素元数据接口，继承自{@link ExecutableDescriptor}，
 * 用于描述可执行元素（方法、构造函数等）的完整元数据信息，包括参数、返回值、异常等。
 * <p>
 * 该接口提供了可执行元素的参数类型匹配判断、名称获取、声明类型描述等核心功能， 是框架中反射操作和动态执行的基础元数据接口。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>参数类型匹配：通过{@link #canExecuted(Class[])}方法实现参数类型兼容性检查</li>
 * <li>完整元数据：提供可执行元素的名称、声明类型、异常类型等完整元数据</li>
 * <li>参数模板访问：通过{@link #getParameterMapping()}获取参数属性描述集合</li>
 * <li>类型安全：通过{@link TypeDescriptor}确保元数据的类型安全性</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>动态方法调用：在运行时根据参数类型选择合适的可执行元素</li>
 * <li>反射工具：获取可执行元素的完整元数据信息</li>
 * <li>框架插件系统：描述插件方法的元数据以实现动态加载</li>
 * <li>参数校验：在执行前校验参数类型是否匹配</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ExecutableDescriptor
 * @see PropertyDescriptor
 * @see TypeDescriptor
 */
public interface ExecutableMetadata extends ExecutableDescriptor {

	/**
	 * 判断可执行元素是否可以使用指定参数类型执行
	 * <p>
	 * 该方法通过以下步骤实现参数类型匹配检查：
	 * <ol>
	 * <li>遍历可执行元素的参数模板和传入的参数类型</li>
	 * <li>检查每个参数类型是否与参数模板的返回类型兼容</li>
	 * <li>要求参数数量和类型完全匹配</li>
	 * </ol>
	 * 
	 * @param parameterTypes 待检查的参数类型数组，不可为null
	 * @return 若参数类型完全匹配返回true，否则返回false
	 */
	@Override
	default boolean canExecuted(@NonNull Class<?>... parameterTypes) {
		return getParameterMapping().elements().equalsInOrder(Streamable.array(parameterTypes),
				(parameterDescriptor, type) -> {
					return type.isAssignableFrom(parameterDescriptor.getReturnTypeDescriptor().getType());
				});
	}

	/**
	 * 获取可执行元素的名称
	 * <p>
	 * 对于方法，返回方法名；对于构造函数，返回类名。
	 * 
	 * @return 可执行元素的名称
	 */
	String getName();

	/**
	 * 获取可执行元素的声明类型描述符
	 * <p>
	 * 返回声明该可执行元素的类或接口的类型描述符， 对于构造函数，返回所属类的类型描述符。
	 * 
	 * @return 声明类型的描述符
	 */
	TypeDescriptor getDeclaringTypeDescriptor();

	/**
	 * 获取可执行元素声明抛出的异常类型描述符集合
	 * <p>
	 * 返回该可执行元素在声明中抛出的所有异常类型的描述符， 对于构造函数，返回其声明抛出的异常类型。
	 * 
	 * @return 异常类型描述符的元素集合
	 */
	Streamable<TypeDescriptor> getExceptionTypeDescriptors();

	/**
	 * 获取可执行元素的参数模板
	 * <p>
	 * 返回包含所有参数属性描述符的模板，每个描述符对应一个参数， 可用于获取参数的名称、类型等元数据信息。
	 * 
	 * @return 参数模板
	 */
	PropertyMapping<PropertyDescriptor> getParameterMapping();
}