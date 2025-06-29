package run.soeasy.framework.core.domain;

import lombok.NonNull;

/**
 * 对象包装器接口
 * 定义了一种递归解包机制，允许将对象层层包装并按需解包
 * 常用于实现装饰器模式或适配不同接口的场景
 *
 * @author shuchaowen
 *
 * @param <T> 被包装的源对象类型
 */
@FunctionalInterface
public interface Wrapper<T> {

    /**
     * 检查对象是否直接或间接包装了指定类型
     * 支持递归检查多层包装
     * 
     * @param source 待检查的对象
     * @param requiredType 目标类型，不能为null
     * @return 如果对象是requiredType的实例，或是包装了requiredType的Wrapper，返回true
     */
    public static boolean isWrapperFor(Object source, @NonNull Class<?> requiredType) {
        if (source == null) {
            return false;
        }

        if (requiredType.isInstance(source)) {
            return true;
        }

        if (source instanceof Wrapper) {
            return ((Wrapper<?>) source).isWrapperFor(requiredType);
        }
        return false;
    }

    /**
     * 尝试从对象中解包出指定类型的实例
     * 支持递归解包多层包装
     * 
     * @param <T> 解包目标类型
     * @param source 待解包的对象
     * @param requiredType 目标类型，不能为null
     * @return 如果对象是requiredType的实例，直接返回
     *         如果对象是包装了requiredType的Wrapper，返回被包装的实例
     *         如果无法解包，返回null
     */
    public static <T> T unwrap(Object source, @NonNull Class<? extends T> requiredType) {
        if (source == null) {
            return null;
        }

        if (requiredType.isInstance(source)) {
            return requiredType.cast(source);
        }

        if (source instanceof Wrapper) {
            Wrapper<?> wrapper = (Wrapper<?>) source;
            return wrapper.unwrap(requiredType);
        }
        return null;
    }

    /**
     * 获取被包装的原始对象
     * 
     * @return 被包装的源对象
     */
    T getSource();

    /**
     * 检查当前包装器是否直接或间接包装了指定类型
     * 先检查自身是否是requiredType的实例
     * 再检查被包装的源对象
     * 如果源对象还是Wrapper，则递归检查
     * 
     * @param requiredType 目标类型，不能为null
     * @return 如果包装器本身或被包装的对象是requiredType的实例，返回true
     */
    default boolean isWrapperFor(@NonNull Class<?> requiredType) {
        if (requiredType.isInstance(this)) {
            return true;
        }

        T source = getSource();
        if (requiredType.isInstance(source)) {
            return true;
        }

        if (source instanceof Wrapper) {
            return ((Wrapper<?>) source).isWrapperFor(requiredType);
        }
        return false;
    }

    /**
     * 尝试从当前包装器中解包出指定类型的实例
     * 先检查自身是否是requiredType的实例
     * 再检查被包装的源对象
     * 如果源对象还是Wrapper，则递归解包
     * 
     * @param <S> 解包目标类型
     * @param requiredType 目标类型，不能为null
     * @return 如果包装器本身是requiredType的实例，直接返回
     *         如果被包装的对象是requiredType的实例，返回该对象
     *         如果被包装的对象是Wrapper且能解包出requiredType，返回解包结果
     * @throws IllegalArgumentException 如果无法解包出指定类型
     */
    @SuppressWarnings("unchecked")
    default <S> S unwrap(@NonNull Class<? extends S> requiredType) {
        if (requiredType.isInstance(this)) {
            return (S) this;
        }

        T source = getSource();
        if (requiredType.isInstance(source)) {
            return (S) source;
        }

        if (source instanceof Wrapper) {
            return ((Wrapper<T>) source).unwrap(requiredType);
        }

        throw new IllegalArgumentException("Cannot unwrap for " + requiredType);
    }
}