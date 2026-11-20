package run.soeasy.framework.core.exchange;

import lombok.NonNull;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 注册表接口 定义元素注册/取消注册和管理的标准行为 注：实现者可自主决定是否支持取消注册（deregister）、是否允许空元素注册
 * 
 * @author soeasy.run
 * @param <E> 注册表中存储的元素类型
 */
public interface Registry<E> extends Streamable<E> {
	/**
	 * 注册单个元素
	 * 
	 * @param element 待注册的元素
	 * @return 注册操作句柄（成功/失败状态+异常原因）
	 */
	Signal register(E element);

	/**
	 * 批量注册元素（默认AND模式：所有成功才成功）
	 * 
	 * @param elements 待注册的元素集合（非null）
	 * @return 批量注册复合句柄（AND模式）
	 */
	default Signal registerAll(@NonNull Streamable<? extends E> elements) {
		return Signal.batch(elements.map(this::register));
	}

	/**
	 * 取消单个元素的注册
	 * 
	 * @param element 待取消注册的元素
	 * @return 取消操作句柄（成功/失败状态+异常原因）
	 */
	Signal deregister(E element);

	/**
	 * 批量取消元素注册（默认AND模式：所有成功才成功）
	 * 
	 * @param elements 待取消注册的元素集合（非null）
	 * @return 批量取消复合句柄（AND模式）
	 */
	default Signal deregisterAll(@NonNull Streamable<? extends E> elements) {
		return Signal.batch(elements.map(this::deregister));
	}

	/**
	 * 重置注册表（取消所有已注册元素，AND模式：所有元素注销成功才视为重置成功）
	 * 
	 * @return 重置操作复合句柄
	 */
	default Signal reset() {
		return deregisterAll(this);
	}
}