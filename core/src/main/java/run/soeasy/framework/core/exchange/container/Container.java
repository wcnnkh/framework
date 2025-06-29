package run.soeasy.framework.core.exchange.container;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Registrations;

/**
 * 容器接口 定义元素注册、取消注册和管理的标准行为，继承自Registry和Registrations
 * 
 * @author soeasy.run
 *
 * @param <E> 容器中存储的元素类型
 * @param <R> 元素注册的句柄类型，需继承PayloadRegistration
 */
public interface Container<E, R extends PayloadRegistration<E>> extends Registry<E>, Registrations<R> {
	/**
	 * 取消单个元素的注册
	 * 
	 * @param element 待取消注册的元素
	 * @return 取消操作的回执
	 */
	default Receipt deregister(E element) {
		return deregisters(Elements.singleton(element));
	}

	/**
	 * 批量取消元素注册（只要有一个成功即视为成功）
	 * 
	 * @param elements 待取消注册的元素集合
	 * @return 取消操作的回执
	 */
	Receipt deregisters(Elements<? extends E> elements);

	/**
	 * 判断容器是否为空
	 * 
	 * @return 容器为空返回true，否则返回false
	 */
	@Override
	default boolean isEmpty() {
		return Registry.super.isEmpty();
	}

	/**
	 * 重置容器（取消所有注册）
	 */
	default void reset() {
		deregisters(this);
	}

	/**
	 * 转换容器元素类型
	 * 
	 * @param codec 类型转换编解码器，不可为null
	 * @param <T>   目标元素类型
	 * @return 转换后的新容器
	 */
	@Override
	default <T> Registry<T> map(@NonNull Codec<T, E> codec) {
		return new MappedContainer<>(this, codec);
	}

	/**
	 * 判断容器是否已取消
	 * 
	 * @return 已取消返回true，否则返回false
	 */
	@Override
	default boolean isCancelled() {
		return isEmpty();
	}

	/**
	 * 判断容器是否可取消
	 * 
	 * @return 可取消返回true，否则返回false
	 */
	@Override
	default boolean isCancellable() {
		return !isEmpty();
	}

	/**
	 * 取消容器中所有元素的注册
	 * 
	 * @return 取消操作是否成功
	 */
	@Override
	default boolean cancel() {
		if (isEmpty()) {
			return false;
		}

		reset();
		return true;
	}
}