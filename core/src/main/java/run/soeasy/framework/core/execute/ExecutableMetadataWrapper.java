package run.soeasy.framework.core.execute;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 可执行元数据包装器函数式接口，继承自{@link ExecutableMetadata}和{@link ExecutableDescriptorWrapper}，
 * 采用装饰器模式对可执行元数据进行包装，支持在不修改原始元数据的前提下为其添加额外信息或行为。
 * <p>
 * 该接口作为函数式接口，允许通过Lambda表达式或方法引用来简洁地实现包装逻辑，
 * 适用于需要动态增强可执行元素元数据的场景，如添加自定义注解、修改参数描述等。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：包装原始可执行元数据并扩展其功能，符合"开闭原则"</li>
 *   <li>函数式接口：支持Lambda表达式实现，简化包装逻辑的编码</li>
 *   <li>方法转发：默认方法将调用转发给被包装的源可执行元数据，保持原始行为</li>
 *   <li>类型安全：通过泛型约束确保包装器与被包装对象的类型一致性</li>
 * </ul>
 * </p>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code W}：被包装的可执行元数据类型，需实现{@link ExecutableMetadata}</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>元数据增强：在运行时为可执行元素添加额外的元数据信息</li>
 *   <li>注解处理：根据自定义注解修改元数据行为</li>
 *   <li>参数重命名：动态修改参数名称以适配不同场景</li>
 *   <li>权限过滤：根据访问权限隐藏某些方法或参数</li>
 *   <li>文档生成：为API文档生成提供额外的元数据信息</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see ExecutableMetadata
 * @see ExecutableDescriptorWrapper
 */
@FunctionalInterface
public interface ExecutableMetadataWrapper<W extends ExecutableMetadata>
        extends ExecutableMetadata, ExecutableDescriptorWrapper<W> {

    /**
     * 判断被包装的可执行元素是否可以使用指定参数类型执行
     * <p>
     * 该默认实现将调用转发给被包装的源可执行元数据的{@link ExecutableMetadata#canExecuted(Class[])}方法，
     * 子类可覆盖此方法添加额外的判断逻辑（如参数验证）。
     * </p>
     * 
     * @param parameterTypes 参数类型数组，不可为null
     * @return 若源可执行元素可以接受指定参数类型返回true，否则返回false
     */
    @Override
    default boolean canExecuted(@NonNull Class<?>... parameterTypes) {
        return getSource().canExecuted(parameterTypes);
    }

    /**
     * 获取被包装的可执行元素的声明类型描述符
     * <p>
     * 该默认实现将调用转发给被包装的源可执行元数据的{@link ExecutableMetadata#getDeclaringTypeDescriptor()}方法，
     * 子类可覆盖此方法返回自定义的声明类型描述符。
     * </p>
     * 
     * @return 声明类型的描述符
     */
    @Override
    default TypeDescriptor getDeclaringTypeDescriptor() {
        return getSource().getDeclaringTypeDescriptor();
    }

    /**
     * 获取被包装的可执行元素声明抛出的异常类型描述符集合
     * <p>
     * 该默认实现将调用转发给被包装的源可执行元数据的{@link ExecutableMetadata#getExceptionTypeDescriptors()}方法，
     * 子类可覆盖此方法添加或过滤异常类型描述符。
     * </p>
     * 
     * @return 异常类型描述符的元素集合
     */
    @Override
    default Elements<TypeDescriptor> getExceptionTypeDescriptors() {
        return getSource().getExceptionTypeDescriptors();
    }

    /**
     * 获取被包装的可执行元素的名称
     * <p>
     * 该默认实现将调用转发给被包装的源可执行元数据的{@link ExecutableMetadata#getName()}方法，
     * 子类可覆盖此方法返回自定义的名称（如别名）。
     * </p>
     * 
     * @return 可执行元素的名称
     */
    @Override
    default String getName() {
        return getSource().getName();
    }

    /**
     * 获取被包装的可执行元素的参数模板
     * <p>
     * 该默认实现将调用转发给被包装的源可执行元数据的{@link ExecutableMetadata#getParameterTemplate()}方法，
     * 子类可覆盖此方法返回自定义的参数模板（如包装后的参数描述符）。
     * </p>
     * 
     * @return 参数模板
     */
    @Override
    default ParameterTemplate getParameterTemplate() {
        return getSource().getParameterTemplate();
    }
}