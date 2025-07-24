package run.soeasy.framework.core.convert.value;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 类型化值访问器包装器接口，用于装饰{@link TypedValueAccessor}实现，支持透明代理和功能增强。
 * <p>
 * 该函数式接口继承自{@link TypedValueAccessor}、{@link TypedValueWrapper}和{@link TypedDataAccessorWrapper}，
 * 允许通过包装现有类型化值访问器实例来添加额外逻辑，同时保持接口的透明性，
 * 适用于需要对类型化值访问进行非侵入式增强的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>透明代理：默认实现将所有方法调用转发给被包装的类型化值访问器</li>
 *   <li>功能增强：支持添加日志记录、缓存优化、转换增强等额外功能</li>
 *   <li>类型安全：泛型参数确保包装器与被包装对象的类型一致性</li>
 *   <li>函数式支持：作为函数式接口，可通过lambda表达式快速创建轻量级包装器</li>
 * </ul>
 *
 * @param <W> 被包装的类型化值访问器类型，需实现{@link TypedValueAccessor}
 * 
 * @author soeasy.run
 * @see TypedValueAccessor
 * @see TypedValueWrapper
 * @see TypedDataAccessorWrapper
 */
@FunctionalInterface
public interface TypedValueAccessorWrapper<W extends TypedValueAccessor>
        extends TypedValueAccessor, TypedValueWrapper<W>, TypedDataAccessorWrapper<Object, W> {

    /**
     * 转换为指定类型的可访问类型化值（透明代理实现）
     * <p>
     * 该默认实现将调用转发给被包装的类型化值访问器实例，
     * 子类可重写此方法以添加自定义逻辑（如转换日志记录、权限校验等）。
     * 
     * @param <R>        目标类型
     * @param type       目标类型Class，不可为null
     * @param converter  转换器，不可为null
     * @return 转换后的可访问类型化值实例
     * @throws NullPointerException 若type或converter为null
     */
    @Override
    default <R> TypedDataAccessor<R> map(@NonNull Class<R> type, @NonNull Converter converter) {
        return getSource().map(type, converter);
    }

    /**
     * 转换为指定类型的可访问类型化值（透明代理实现）
     * <p>
     * 该默认实现将调用转发给被包装的类型化值访问器实例，
     * 子类可重写此方法以添加自定义逻辑（如转换性能监控、异常处理等）。
     * 
     * @param typeDescriptor 目标类型描述符，不可为null
     * @param converter      转换器，不可为null
     * @return 转换后的可访问类型化值实例
     * @throws NullPointerException 若typeDescriptor或converter为null
     */
    @Override
    default TypedValueAccessor map(@NonNull TypeDescriptor typeDescriptor, @NonNull Converter converter) {
        return getSource().map(typeDescriptor, converter);
    }
}