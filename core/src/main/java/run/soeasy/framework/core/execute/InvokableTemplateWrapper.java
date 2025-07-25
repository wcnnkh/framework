package run.soeasy.framework.core.execute;

import lombok.NonNull;

/**
 * 可调用模板包装器函数式接口，继承自{@link InvokableTemplate}和{@link ExecutableDescriptorWrapper}，
 * 采用装饰器模式对可调用模板进行包装，支持在不修改原始逻辑的前提下为方法调用添加额外功能。
 * <p>
 * 该接口作为函数式接口，允许通过Lambda表达式或方法引用来简洁地实现包装逻辑，
 * 适用于需要动态增强方法调用行为的场景，如参数验证、返回值处理、异常拦截等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：包装原始可调用模板并扩展其功能，符合"开闭原则"</li>
 *   <li>函数式接口：支持Lambda表达式实现，简化包装逻辑的编码</li>
 *   <li>方法转发：默认方法将调用转发给被包装的源可调用模板，保持原始行为</li>
 *   <li>类型安全：通过泛型约束确保包装器与被包装对象的类型一致性</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code W}：被包装的可调用模板类型，需实现{@link InvokableTemplate}</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>参数校验：在方法调用前验证参数合法性</li>
 *   <li>返回值处理：对方法返回值进行转换或增强</li>
 *   <li>异常拦截：捕获并处理方法抛出的异常</li>
 *   <li>性能监控：记录方法执行时间和性能指标</li>
 *   <li>安全控制：在方法调用前验证访问权限</li>
 * </ul>
 *
 * @author soeasy.run
 * @see InvokableTemplate
 * @see ExecutableDescriptorWrapper
 */
@FunctionalInterface
public interface InvokableTemplateWrapper<W extends InvokableTemplate> extends InvokableTemplate, ExecutableDescriptorWrapper<W> {

    /**
     * 无参数调用被包装的可执行元素
     * <p>
     * 该默认实现将调用转发给被包装的源可调用模板的{@link InvokableTemplate#invoke(Object)}方法，
     * 子类可覆盖此方法添加前置处理（如目标对象验证）或后置处理（如结果缓存）。
     * 
     * @param target 目标对象，对于静态方法可为null
     * @return 方法调用结果
     * @throws Throwable 调用过程中抛出的任何异常
     */
    @Override
    default Object invoke(Object target) throws Throwable {
        return getSource().invoke(target);
    }

    /**
     * 带参数调用被包装的可执行元素
     * <p>
     * 该默认实现将调用转发给被包装的源可调用模板的{@link InvokableTemplate#invoke(Object, Class[], Object...)}方法，
     * 子类可覆盖此方法添加额外逻辑，如参数转换、异常处理、性能监控等。
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
}