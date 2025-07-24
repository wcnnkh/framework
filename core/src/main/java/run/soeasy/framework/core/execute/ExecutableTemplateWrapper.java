package run.soeasy.framework.core.execute;

import lombok.NonNull;

/**
 * 可执行模板包装器函数式接口，继承自{@link ExecutableTemplate}和{@link ExecutableDescriptorWrapper}，
 * 采用装饰器模式对可执行模板进行包装，支持在不修改原始逻辑的前提下为可执行元素添加额外功能。
 * <p>
 * 该接口作为函数式接口，允许通过Lambda表达式或方法引用来简洁地实现包装逻辑，
 * 适用于需要动态增强可执行元素行为的场景，如日志记录、权限控制等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：包装原始可执行模板并扩展其功能，符合"开闭原则"</li>
 *   <li>函数式接口：支持Lambda表达式实现，简化包装逻辑的编码</li>
 *   <li>方法转发：默认方法将调用转发给被包装的源可执行模板，保持原始行为</li>
 *   <li>类型安全：通过泛型约束确保包装器与被包装对象的类型一致性</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code W}：被包装的可执行模板类型，需实现{@link ExecutableTemplate}</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>日志增强：在执行前后记录方法调用日志</li>
 *   <li>权限控制：执行前校验调用者权限</li>
 *   <li>性能监控：测量方法执行耗时并记录性能指标</li>
 *   <li>事务管理：为方法调用添加事务控制逻辑</li>
 *   <li>参数校验：执行前验证输入参数的合法性</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ExecutableTemplate
 * @see ExecutableDescriptorWrapper
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface ExecutableTemplateWrapper<W extends ExecutableTemplate>
        extends ExecutableTemplate, ExecutableDescriptorWrapper<W> {

    /**
     * 判断被包装的可执行元素是否可以执行（无参）
     * <p>
     * 该默认实现将调用转发给被包装的源可执行模板的{@link ExecutableTemplate#canExecuted()}方法，
     * 子类可覆盖此方法添加额外的判断逻辑（如权限校验）。
     * 
     * @return 若源可执行元素可以执行返回true，否则返回false
     */
    @Override
    default boolean canExecuted() {
        return getSource().canExecuted();
    }

    /**
     * 执行被包装的可执行元素（带参数）
     * <p>
     * 该默认实现将调用转发给被包装的源可执行模板的{@link ExecutableTemplate#execute(Class[], Object...)}方法，
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
}