package run.soeasy.framework.core.exchange2;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 组合信号容器：持有一组子信号 + 一条组合规则 {@link CompositePattern}
 *
 * <p>
 * 本身是 {@link Signal} 的子类型，叶子信号与组合信号同质，支持无限嵌套编排； 所有状态查询、等待、取消、回滚逻辑全部委托给内部的
 * {@link CompositePattern} 实现。
 * </p>
 *
 * <p>
 * 核心模型：
 * <ul>
 * <li>容器只负责持有子信号与模式，不维护独立状态；状态是实时聚合视图，读取子信号瞬时状态</li>
 * <li>不同模式拥有独立的超时语义，调用 {@link #await(long, TimeUnit)} 时务必区分串行/并行策略</li>
 * </ul>
 * </p>
 *
 * @param <T> 子信号类型
 */
public interface Signals<T extends Signal> extends Signal {

	/**
	 * 组合模式：定义一组信号的聚合规则、等待调度策略
	 *
	 * <p>
	 * 【超时策略约定，和 Signal 顶层文档对齐】
	 * </p>
	 * <ul>
	 * <li>{@link Signal.SequentialAnd}：timeout =
	 * 整条链路共享总时间预算，任务串行依次消耗，剩余时间向后传递，目标是链路整体实时可控</li>
	 * <li>{@link Signal.ParallelAnd} / {@link Signal.ParallelOr}：timeout =
	 * <b>每个子信号独立兜底时长</b>，所有子信号拿到完整时限，时间不互相瓜分；目标是防止子信号永久阻塞，避免线程池泄漏</li>
	 * </ul>
	 *
	 * <p>
	 * 等待契约：
	 * </p>
	 * <ul>
	 * <li>await()：无超时阻塞，直到满足组合完成条件；仅抛出 {@link InterruptedException}</li>
	 * <li>await(timeout, unit)：返回 false
	 * 仅代表【本次等待侧超时】，<b>不会自动取消后台正在运行的子信号</b>；子信号继续执行直到自身完成或子信号兜底超时</li>
	 * </ul>
	 *
	 * <p>
	 * 取消 & 回滚：
	 * </p>
	 * <ul>
	 * <li>cancel：广播下发给全部子信号，由模式聚合返回结果（And：全部取消成功才返回true；Or：任意一个取消成功返回true）</li>
	 * <li>isRollbackSupported / isRolledBack：同样按 And/Or 语义聚合子信号能力与状态</li>
	 * </ul>
	 */
	static interface CompositePattern {
		/**
		 * 判断这批子信号是否满足组合成功条件
		 *
		 * @param elements 子信号集合
		 * @return true 满足成功条件
		 */
		boolean isSuccess(@NonNull Streamable<? extends Signal> elements);

		/**
		 * 判断这批子信号是否满足组合完成条件
		 *
		 * @param elements 子信号集合
		 * @return true 满足完成条件
		 */
		boolean isDone(@NonNull Streamable<? extends Signal> elements);

		/**
		 * 阻塞等待，直到满足该组合模式的完成条件
		 *
		 * @param elements 子信号集合
		 * @throws InterruptedException 等待线程被中断
		 */
		void await(@NonNull Streamable<? extends Signal> elements) throws InterruptedException;

		/**
		 * 带超时阻塞等待，直到满足组合完成条件或等待超时
		 *
		 * @param elements 子信号集合
		 * @param timeout  超时时长
		 * @param unit     时间单位
		 * @return true 在超时前达成完成条件；false 等待超时
		 * @throws InterruptedException 等待线程被中断
		 */
		boolean await(@NonNull Streamable<? extends Signal> elements, long timeout, TimeUnit unit)
				throws InterruptedException;

		/**
		 * 广播取消所有子信号，按模式聚合返回结果
		 *
		 * @param elements              子信号集合
		 * @param mayInterruptIfRunning 是否允许中断正在执行的任务
		 * @return And：全部取消成功返回true；Or：任意一个取消成功返回true
		 */
		boolean cancel(@NonNull Streamable<? extends Signal> elements, boolean mayInterruptIfRunning);

		/**
		 * 聚合判断是否被取消
		 */
		boolean isCancelled(@NonNull Streamable<? extends Signal> elements);

		/**
		 * 聚合判断是否支持回滚能力
		 */
		boolean isRollbackSupported(@NonNull Streamable<? extends Signal> elements);

		/**
		 * 聚合判断是否已经完成回滚
		 */
		boolean isRolledBack(@NonNull Streamable<? extends Signal> elements);
	}

	/**
	 * 默认实现：组合信号容器，绑定一条组合规则 + 子信号流
	 *
	 * @param <E> 子信号类型
	 * @param <C> 组合模式类型
	 */
	@RequiredArgsConstructor
	@Getter
	public static class CompositeSignals<E extends Signal, C extends CompositePattern> implements Signals<E> {
		/** 组合规则：And / Or 实现 */
		private final C compositePattern;
		/** 子信号集合，惰性流，状态查询时实时读取 */
		private final Streamable<E> elements;
	}

	/**
	 * 获取当前绑定的组合模式
	 *
	 * @return 组合模式
	 */
	CompositePattern getCompositePattern();

	/**
	 * 获取当前包含的子信号流（惰性）
	 *
	 * @return 子信号流
	 */
	Streamable<T> getElements();

	/**
	 * 委托组合模式聚合判断是否成功
	 *
	 * @return 是否满足组合成功条件
	 */
	@Override
	default boolean isSuccess() {
		return getCompositePattern().isSuccess(getElements());
	}

	/**
	 * 委托组合模式聚合判断是否完成终态
	 *
	 * @return 是否满足组合完成条件
	 */
	@Override
	default boolean isDone() {
		return getCompositePattern().isDone(getElements());
	}

	/**
	 * 按当前组合规则阻塞等待至完成
	 *
	 * @throws InterruptedException 等待线程被中断
	 */
	@Override
	default void await() throws InterruptedException {
		getCompositePattern().await(getElements());
	}

	/**
	 * 按当前组合规则带超时等待至完成
	 * <p>
	 * 注意：串行、并行模式的 timeout 语义不一样，参考 {@link CompositePattern} 文档说明
	 * </p>
	 *
	 * @param timeout 超时时长
	 * @param unit    时间单位
	 * @return true 在超时前完成；false 等待超时
	 * @throws InterruptedException 等待线程被中断
	 */
	@Override
	default boolean await(long timeout, @NonNull TimeUnit unit) throws InterruptedException {
		return getCompositePattern().await(getElements(), timeout, unit);
	}

	/**
	 * 扩展组合优化： 如果追加的组合规则和当前容器规则一致，直接合并子信号，消除一层嵌套，避免包装膨胀； 规则不一致时，回退到 Signal 原生
	 * composite 逻辑新建组合。
	 *
	 * @param compositePattern 目标组合规则
	 * @param others           追加的信号
	 * @return 合并后的组合信号
	 */
	@Override
	default Signal composite(@NonNull CompositePattern compositePattern, @NonNull Signal... others) {
		if (getCompositePattern().equals(compositePattern)) {
			return new CompositeSignals<>(compositePattern, getElements().mapPure(Signal.class::cast).concat(others));
		}
		return Signal.super.composite(compositePattern, others);
	}

	/**
	 * 同步等待完成，协变返回当前 Signals 容器，而不是父接口 Signal
	 *
	 * @return this
	 */
	@Override
	default Signals<T> sync() {
		Signal.super.sync();
		return this;
	}

	/**
	 * 生成回滚链路：对每个子信号调用 rollback()，再复用当前组合规则把回滚信号组合起来
	 * <p>
	 * 只是构造回滚信号编排，不会自动执行；需要外部调用 await() 才会阻塞执行回滚
	 * </p>
	 *
	 * @return 回滚链路组合信号
	 */
	@Override
	default Signal rollback() {
		List<Signal> list = getElements().mapPure((e) -> e.rollback()).toList();
		return Signal.composite(getCompositePattern(), Streamable.of(list));
	}

	/**
	 * 广播取消全部子信号，按当前组合模式聚合结果
	 */
	@Override
	default boolean cancel(boolean mayInterruptIfRunning) {
		return getCompositePattern().cancel(getElements(), mayInterruptIfRunning);
	}

	/**
	 * 聚合判断是否被取消
	 */
	@Override
	default boolean isCancelled() {
		return getCompositePattern().isCancelled(getElements());
	}

	/**
	 * 聚合判断是否支持回滚
	 */
	@Override
	default boolean isRollbackSupported() {
		return getCompositePattern().isRollbackSupported(getElements());
	}

	/**
	 * 聚合判断是否全部（And）/任意（Or）完成回滚
	 */
	@Override
	default boolean isRolledBack() {
		return getCompositePattern().isRolledBack(getElements());
	}

	/**
	 * 【容器重写】boxed：把当前组合信号再包装一层，作为单元素组合。
	 * <p>
	 * 注意：这是嵌套包装，不是拍平；适合需要统一“所有节点都为组合容器”的场景。
	 * @return 包装后的单元素组合信号
	 */
	@Override
	default Signal boxed() {
	    return new CompositeSignals<>(getCompositePattern(), Streamable.singleton(this));
	}

	/**
	 * 【容器重写】unboxed：直接返回自身，强类型Signals，暴露子元素与组合模式。
	 * <p>
	 * 叶子Signal调用unboxed → 包装为单元素容器；
	 * 已经是Signals容器调用unboxed → 直接返回自身，避免重复嵌套。
	 * @return 当前组合容器
	 */
	@Override
	default Signals<? extends Signal> unboxed() {
	    return new CompositeSignals<>(getCompositePattern(), getElements());
	}

}
