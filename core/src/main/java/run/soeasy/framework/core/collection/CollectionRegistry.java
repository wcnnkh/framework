package run.soeasy.framework.core.collection;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;

import run.soeasy.framework.core.exchange.Registry;
import run.soeasy.framework.core.exchange.Signal;

/**
 * 集合型注册中心接口（集成Collection与Registry能力）
 * <p>
 * 核心能力： 1. 基于Collection的增删能力实现注册/注销语义，支持操作回滚； 2. 重置操作清空整个集合，返回标准化Operation结果；
 * 3. 所有操作结果适配Operation接口规范（成功/失败/回滚）。
 * 
 * @author soeasy.run
 * @param <E> 注册元素类型
 */
public interface CollectionRegistry<E> extends Collection<E>, Registry<E> {

	/**
	 * 注册元素（添加到集合），支持回滚（回滚逻辑：移除该元素）
	 * 
	 * @param element 待注册元素（非null）
	 * @return Operation 操作结果： - 成功：元素添加成功，支持回滚（移除元素）； -
	 *         失败：元素为null/添加失败/抛出异常（含失败原因）。
	 */
	@Override
	default Signal register(E element) {
		// 空值校验：封装为异常传给failure
		if (element == null) {
			return Signal.promise().failure(new RuntimeException("Register failed: element is null"));
		}
		return Signal.run(() -> {
			if (!add(element)) {
				throw new RuntimeException(
						"Register failed: element already exists or add rejected, element=" + element);
			}
		}).rollback((e) -> this.deregister(element));
	}

	/**
	 * 注销元素（从集合移除），支持回滚（回滚逻辑：重新添加该元素）
	 * 
	 * @param element 待注销元素（非null）
	 * @return Operation 操作结果： - 成功：元素移除成功，支持回滚（重新添加）； -
	 *         失败：元素为null/元素不存在/移除失败/抛出异常（含失败原因）。
	 */
	@Override
	default Signal deregister(E element) {
		// 空值校验：封装为异常传给failure
		if (element == null) {
			return Signal.promise().failure(new RuntimeException("Deregister failed: element is null"));
		}
		return Signal.run(() -> {
			if (!remove(element)) {
				throw new RuntimeException(
						"Deregister failed: element not found or remove rejected, element=" + element);
			}
		}).rollback((e) -> register(element));
	}

	/**
	 * 重置注册中心（清空所有元素），无回滚逻辑
	 * 
	 * @return Operation 操作结果： - 成功：集合清空完成； - 失败：清空操作抛出异常（含失败原因）。
	 */
	@Override
	default Signal reset() {
		return Signal.run(() -> clear());
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