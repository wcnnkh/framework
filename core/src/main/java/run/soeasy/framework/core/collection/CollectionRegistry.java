package run.soeasy.framework.core.collection;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;

import run.soeasy.framework.core.exchange.Operation;
import run.soeasy.framework.core.exchange.Registry;

/**
 * 集合型注册中心接口（集成Collection与Registry能力）
 * <p>核心能力：
 * 1. 基于Collection的增删能力实现注册/注销语义，支持操作回滚；
 * 2. 重置操作清空整个集合，返回标准化Operation结果；
 * 3. 所有操作结果适配Operation接口规范（成功/失败/回滚）。
 * 
 * @author soeasy.run
 * @param <E> 注册元素类型
 */
public interface CollectionRegistry<E> extends Collection<E>, Registry<E> {

    /**
     * 注册元素（添加到集合），支持回滚（回滚逻辑：移除该元素）
     * @param element 待注册元素（非null）
     * @return Operation 操作结果：
     *         - 成功：元素添加成功，支持回滚（移除元素）；
     *         - 失败：元素为null/添加失败/抛出异常（含失败原因）。
     */
    @Override
    default Operation register(E element) {
        // 空值校验：封装为异常传给failure
        if (element == null) {
            return Operation.failure(new RuntimeException("Register failed: element is null"));
        }
        try {
            // 复用Collection的add方法实现注册
            boolean added = add(element);
            if (added) {
                // 注册成功：添加回滚逻辑（移除当前元素）
                return Operation.success(() -> deregister(element).isSuccess());
            } else {
                // 添加失败：封装失败原因
                return Operation.failure(new RuntimeException(
                        "Register failed: element already exists or add rejected, element=" + element));
            }
        } catch (Exception e) {
            // 捕获运行时异常（如UnsupportedOperationException、ClassCastException等）
            return Operation.failure(e);
        }
    }

    /**
     * 注销元素（从集合移除），支持回滚（回滚逻辑：重新添加该元素）
     * @param element 待注销元素（非null）
     * @return Operation 操作结果：
     *         - 成功：元素移除成功，支持回滚（重新添加）；
     *         - 失败：元素为null/元素不存在/移除失败/抛出异常（含失败原因）。
     */
    @Override
    default Operation deregister(E element) {
        // 空值校验：封装为异常传给failure
        if (element == null) {
            return Operation.failure(new RuntimeException("Deregister failed: element is null"));
        }
        try {
            // 复用Collection的remove方法实现注销
            boolean removed = remove(element);
            if (removed) {
                // 注销成功：添加回滚逻辑（重新添加当前元素）
                return Operation.success(() -> register(element).isSuccess());
            } else {
                // 移除失败：封装失败原因
                return Operation.failure(new RuntimeException(
                        "Deregister failed: element not found or remove rejected, element=" + element));
            }
        } catch (Exception e) {
            // 捕获运行时异常
            return Operation.failure(e);
        }
    }

    /**
     * 重置注册中心（清空所有元素），无回滚逻辑
     * @return Operation 操作结果：
     *         - 成功：集合清空完成；
     *         - 失败：清空操作抛出异常（含失败原因）。
     */
    @Override
    default Operation reset() {
        try {
            // 复用Collection的clear方法实现重置
            clear();
            return Operation.success(); // 无回滚的成功操作
        } catch (Exception e) {
            // 捕获清空时的异常（如UnsupportedOperationException）
            return Operation.failure(e);
        }
    }

    @Override
    boolean isEmpty();

    @Override
    boolean contains(Object o);

    @Override
    default void forEach(Consumer<? super E> action) {
        Collection.super.forEach(action);
    }

    @Override
    default Stream<E> stream() {
        return Collection.super.stream();
    }

    @Override
    Object[] toArray();
}