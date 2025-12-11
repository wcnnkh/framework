package run.soeasy.framework.core.exchange;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.CompositeOperation.Mode;
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
	Operation register(E element);

	/**
	 * 批量注册元素（容错模式：单个元素失败不中断整体流程）
	 * 
	 * @param elements 待注册的元素集合（非null）
	 * @param mode     批量结果的组合判定模式（AND：所有成功才成功；OR：任意成功即成功）
	 * @return 批量注册复合句柄（成功判定规则由mode决定）
	 */
	default Operation registerAll(@NonNull Streamable<? extends E> elements, @NonNull Mode mode) {
		return Operation.batch(elements, mode, this::register);
	}

	/**
	 * 批量注册元素（默认AND模式：所有成功才成功）
	 * 
	 * @param elements 待注册的元素集合（非null）
	 * @return 批量注册复合句柄（AND模式）
	 */
	default Operation registerAll(@NonNull Streamable<? extends E> elements) {
		return registerAll(elements, Mode.AND);
	}

	/**
	 * 取消单个元素的注册
	 * 
	 * @param element 待取消注册的元素
	 * @return 取消操作句柄（成功/失败状态+异常原因）
	 */
	Operation deregister(E element);

	/**
	 * 批量取消元素注册（容错模式：单个元素失败不中断整体流程）
	 * 
	 * @param elements 待取消注册的元素集合（非null）
	 * @param mode     批量结果的组合判定模式（AND：所有成功才成功；OR：任意成功即成功）
	 * @return 批量取消复合句柄（成功判定规则由mode决定）
	 */
	default Operation deregisterAll(@NonNull Streamable<? extends E> elements, @NonNull Mode mode) {
		return Operation.batch(elements, mode, this::deregister);
	}

	/**
	 * 批量取消元素注册（默认AND模式：所有成功才成功）
	 * 
	 * @param elements 待取消注册的元素集合（非null）
	 * @return 批量取消复合句柄（AND模式）
	 */
	default Operation deregisterAll(@NonNull Streamable<? extends E> elements) {
		return deregisterAll(elements, Mode.AND);
	}

	/**
	 * 重置注册表（取消所有已注册元素，AND模式：所有元素注销成功才视为重置成功）
	 * 
	 * @return 重置操作复合句柄
	 */
	default Operation reset() {
		return deregisterAll(this, Mode.AND);
	}
}