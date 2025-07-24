package run.soeasy.framework.core.execute;

import lombok.NonNull;

/**
 * 可调用元素包装器函数式接口，继承自{@link InvodableElement}、{@link ExecutableMetadataWrapper}和{@link InvokableTemplateWrapper}，
 * 采用装饰器模式对可调用元素进行包装，支持在不修改原始逻辑的前提下为其添加额外功能。
 * <p>
 * 该接口作为函数式接口，允许通过Lambda表达式或方法引用来简洁地实现包装逻辑，
 * 适用于需要动态增强可调用元素行为的场景，如参数校验、结果转换、异常拦截等。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：包装原始可调用元素并扩展其功能，符合"开闭原则"</li>
 *   <li>函数式接口：支持Lambda表达式实现，简化包装逻辑的编码</li>
 *   <li>方法转发：默认方法将调用转发给被包装的源可调用元素，保持原始行为</li>
 *   <li>多层包装：同时继承元数据包装器和模板包装器，支持全方位功能扩展</li>
 * </ul>
 * </p>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code W}：被包装的可调用元素类型，需实现{@link InvodableElement}</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>参数校验：在方法调用前验证参数合法性</li>
 *   <li>结果转换：对方法返回值进行格式化或转换</li>
 *   <li>异常拦截：捕获并处理方法抛出的异常</li>
 *   <li>性能监控：记录方法执行时间和性能指标</li>
 *   <li>安全控制：在方法调用前验证访问权限</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see InvodableElement
 * @see ExecutableMetadataWrapper
 * @see InvokableTemplateWrapper
 */
@FunctionalInterface
public interface InvodableElementWrapper<W extends InvodableElement>
        extends InvodableElement, ExecutableMetadataWrapper<W>, InvokableTemplateWrapper<W> {

    /**
     * 带参数类型和值调用被包装的可执行元素
     * <p>
     * 该默认实现将调用转发给被包装的源可调用元素的{@link InvodableElement#invoke(Object, Class[], Object...)}方法，
     * 子类可覆盖此方法添加前置处理（如参数转换）或后置处理（如结果缓存）。
     * </p>
     * 
     * @param target 目标对象，对于静态方法可为null
     * @param parameterTypes 参数类型数组，不可为null
     * @param args 参数值数组，不可为null
     * @return 方法调用结果
     * @throws Throwable 调用过程中抛出的任何异常
     */
    @Override
    default Object invoke(Object target, @NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
        return getSource().invoke(target, parameterTypes, args);
    }

    /**
     * 仅带参数值调用被包装的可执行元素
     * <p>
     * 该默认实现将调用转发给被包装的源可调用元素的{@link InvodableElement#invoke(Object, Object...)}方法，
     * 子类可覆盖此方法添加额外逻辑，如参数验证、结果转换等。
     * </p>
     * 
     * @param target 目标对象，对于静态方法可为null
     * @param args 参数值数组，不可为null
     * @return 方法调用结果
     * @throws Throwable 调用过程中抛出的任何异常
     */
    @Override
    default Object invoke(Object target, @NonNull Object... args) throws Throwable {
        return getSource().invoke(target, args);
    }
}