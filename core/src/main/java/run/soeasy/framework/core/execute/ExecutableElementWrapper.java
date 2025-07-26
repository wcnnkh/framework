package run.soeasy.framework.core.execute;

import lombok.NonNull;

/**
 * 可执行元素包装器函数式接口，继承自{@link ExecutableElement}、{@link ExecutableMetadataWrapper}和{@link ExecutableTemplateWrapper}，
 * 采用装饰器模式对可执行元素进行包装，支持在不修改原始逻辑的前提下为其添加额外功能。
 * <p>
 * 该接口作为函数式接口，允许通过Lambda表达式或方法引用来简洁地实现包装逻辑，
 * 适用于需要动态增强可执行元素行为的场景，如日志记录、权限控制、事务管理等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：包装原始可执行元素并扩展其功能，符合"开闭原则"</li>
 *   <li>函数式接口：支持Lambda表达式实现，简化包装逻辑的编码</li>
 *   <li>方法转发：默认方法将调用转发给被包装的源可执行元素，保持原始行为</li>
 *   <li>多层包装：同时继承元数据包装器和模板包装器，支持全方位功能扩展</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>日志增强：在执行前后记录方法调用日志</li>
 *   <li>权限控制：执行前校验调用者权限</li>
 *   <li>性能监控：测量方法执行耗时并记录性能指标</li>
 *   <li>事务管理：为方法调用添加事务控制逻辑</li>
 *   <li>缓存处理：执行前检查缓存，执行后更新缓存</li>
 *   <li>参数校验：执行前验证输入参数的合法性</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ExecutableElement
 * @see ExecutableMetadataWrapper
 * @see ExecutableTemplateWrapper
 */
@FunctionalInterface
public interface ExecutableElementWrapper<W extends ExecutableElement>
        extends ExecutableElement, ExecutableMetadataWrapper<W>, ExecutableTemplateWrapper<W> {

    /**
     * 带参数类型和值执行被包装的可执行元素
     * <p>
     * 该默认实现将调用转发给被包装的源可执行元素的{@link ExecutableElement#execute(Class[], Object...)}方法，
     * 子类可覆盖此方法添加前置处理（如参数转换）或后置处理（如结果包装）。
     * 
     * @param parameterTypes 参数类型数组，不可为null
     * @param args 参数值数组，不可为null
     * @return 可执行元素的执行结果
     * @throws Throwable 执行过程中抛出的任何异常
     */
    @Override
    default Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
        return getSource().execute(parameterTypes, args);
    }

    /**
     * 仅带参数值执行被包装的可执行元素
     * <p>
     * 该默认实现将调用转发给被包装的源可执行元素的{@link ExecutableElement#execute(Object...)}方法，
     * 子类可覆盖此方法添加额外逻辑，如参数验证、结果转换等。
     * 
     * @param args 参数值数组，不可为null
     * @return 可执行元素的执行结果
     * @throws Throwable 执行过程中抛出的任何异常
     */
    @Override
    default Object execute(@NonNull Object... args) throws Throwable {
        return getSource().execute(args);
    }
}