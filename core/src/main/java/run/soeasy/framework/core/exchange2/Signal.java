package run.soeasy.framework.core.exchange2;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.exchange2.Signals.CompositePattern;
import run.soeasy.framework.core.exchange2.Signals.CompositeSignals;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 信号原语：异步任务与编排节点的<b>一次性终态</b>抽象。
 *
 * <p>
 * 本接口统一描述「可等待 / 可取消 / 可回滚 / 可组合」的执行单元。叶子任务与组合信号同质：
 * {@linkplain Signals 组合信号} 本身也是 {@code Signal}，因此可以无限嵌套编排， 
 * 不需要为了「继续编排」而向下转型。
 *
 * <p>
 * <b>一、状态契约</b>
 * <ul>
 * <li>{@link #isDone()}：是否抵达终态；成功、失败、取消、超时都算完成</li>
 * <li>{@link #isSuccess()}：是否正常成功完成；未完成、失败、取消均返回 false</li>
 * <li>不变量：{@code isSuccess() == true} ⇒ {@code isDone() == true}；{@code isCancelled() == true} ⇒ {@code isDone() == true}</li>
 * </ul>
 *
 * <p>
 * <b>二、等待契约（await）</b>
 * <ul>
 * <li>{@link #await()}：阻塞至终态，<b>仅抛出 {@link InterruptedException}</b>。业务异常、取消信息一律不外抛，
 * 全部下沉为状态，由 {@link #isSuccess()}、{@link #isCancelled()} 以及实现类附加接口（如 {@code Stage#getCause}）读取</li>
 * <li>{@link #await(long, TimeUnit)}：带超时等待。返回 true 表示在时限内抵达终态（成功 / 失败 / 取消都算）；
 * 返回 false <b>仅表示等待侧超时</b></li>
 * <li>重要边界：等待侧超时返回 false、或因中断抛出 {@link InterruptedException}，
 * <b>都不代表子任务被取消或终止</b>；正在运行的节点会自行继续，直到自身完成或自身兜底超时到期。
 * 即「等待超时」与「任务终止」是两件独立的事，需要终止必须显式调用 {@link #cancel(boolean)}</li>
 * </ul>
 *
 * <p>
 * <b>三、取消契约（cancel / isCancelled）</b>
 * <ul>
 * <li>语义与 JDK {@link java.util.concurrent.Future} <b>完全对齐</b>，二者是同一套状态，不是两套标记</li>
 * <li>{@link #cancel(boolean)} 返回 true 表示「本次调用成功把目标置为取消态」（此前尚未完成），
 * 与 {@code Future#cancel} 的返回值含义一致</li>
 * <li>实现类（尤其 {@code Stage}，同时实现 {@code Signal} 与 {@code Future}）只维护<b>一份</b>取消状态，
 * 禁止出现两套独立标记</li>
 * </ul>
 *
 * <p>
 * <b>四、回滚契约（rollback）</b>
 * <ul>
 * <li>{@link #rollback()} 每次调用都生成一套独立的补偿信号。框架<b>不记录</b>「是否已发起过回滚」，也不拦截重复调用，
 * 补偿的幂等与防重下沉到叶子实现自行保证</li>
 * <li>{@link #isRollbackSupported()} 是<b>能力维度</b>（是否具备补偿能力）；</li>
 * <li>{@link #isRolledBack()} 是<b>状态维度</b>（补偿是否已执行完成），二者不可混用</li>
 * </ul>
 *
 * <p>
 * <b>五、组合契约（And / Or）</b>
 * <ul>
 * <li>核心原则：<b>动作广播，结果聚合</b>。不论 And 还是 Or，{@code cancel} 与 {@code rollback} 都会下发给
 * <b>全部</b>子信号，组合模式只负责聚合「完成 / 成功 / 取消 / 回滚」的判定结果，
 * 不裁剪任何子节点的动作义务，也不做「胜出分支推导」</li>
 * <li>组合信号是<b>无状态实时视图</b>：不缓存快照，每次状态查询都重新遍历子信号。
 * 并发场景下子信号状态变更，一次聚合可能读到「撕裂状态」；需要稳定快照的调用方应先自行物化子信号集合</li>
 * <li>正向 Or「任一成功即满足」，与「其余子信号已被取消」无关；取消/回滚动作依然广播到全部子节点</li>
 * </ul>
 *
 * <p>
 * <b>六、内置组合模式与超时策略差异（重点，两套策略刻意不同）</b>
 * <ul>
 * <li>{@link SequentialAnd}：串行与。timeout 是<b>整条链路的总时间预算</b>，任务依次消耗、剩余时间递减，
 * 目标是换取链路整体的<b>实时可控</b></li>
 * <li>{@link ParallelAnd}：并行与。timeout 是<b>每个子信号独立的执行兜底上限</b>，各子信号并行起跑、
 * 各自拥有完整时限、互不瓜分，目标是<b>防止子任务永久阻塞拖死线程池</b>，实时性只是附带收益</li>
 * <li>{@link ParallelOr}：并行或。任一子信号完成即返回；<b>其余子信号不会被自动取消</b>，
 * 仍会各自跑完或抵达自身兜底超时</li>
 * </ul>
 * <p>
 * 简言之：串行的 timeout 是「链路总预算」，并行的 timeout 是「单节点兜底上限」。
 * 调用方看到同名 {@code await(timeout, unit)} 时，必须先确认实现的是哪一族模式，切勿按同一模型理解。
 *
 * <p>
 * <b>七、链式 API</b>
 * <ul>
 * <li>{@link #and} / {@link #or} / {@link #composite} 用于快速构建组合信号，语义为「当前信号 + 其他信号」</li>
 * <li>组合信号再次组合时，若模式 {@code equals} 相等则自动拍平子信号，避免无意义的多层嵌套包装</li>
 * </ul>
 */
public interface Signal {

	/**
	 * And 组合模式抽象基类：「全部满足」语义。
	 *
	 * <p>
	 * 状态聚合：全部子信号 {@code isDone} 才算 done；全部 {@code isSuccess} 才算 success；
	 * 全部 {@code isCancelled} 才算 cancelled；{@code isRollbackSupported} / {@code isRolledBack} 同理取全量。
	 *
	 * <p>
	 * 取消策略：向<b>全部</b>子信号广播 {@code cancel}，仅当所有子信号都返回 true 时整体返回 true。
	 * 也就是说「取消动作一定全员下发，返回结果才做聚合」。
	 */
	public static abstract class And extends MatchCompositePattern {
		/**
		 * 向全部子信号广播取消，再对返回结果做 allMatch 聚合。
		 *
		 * @param elements             子信号集合
		 * @param mayInterruptIfRunning 是否允许中断正在执行的线程
		 * @return true 表示全部子信号都被成功取消
		 */
		@Override
		public boolean cancel(@NonNull Streamable<? extends Signal> elements, boolean mayInterruptIfRunning) {
			// 取消所有的，只对结果进行判断
			return elements.mapPure((e) -> e.cancel(mayInterruptIfRunning)).toList().stream()
					.allMatch(Boolean.TRUE::equals);
		}

		/**
		 * 全量匹配：谓词对每个子信号都成立才返回 true。
		 *
		 * @param elements  子信号集合
		 * @param predicate 状态谓词
		 * @return 是否全部满足
		 */
		@Override
		public <E extends Signal> boolean match(Streamable<? extends E> elements, Predicate<? super E> predicate) {
			return elements.allMatch(predicate);
		}
	}

	/**
	 * 已就绪固定信号：创建时即为终态，状态永不变化。
	 *
	 * <p>
	 * 语义：
	 * <ul>
	 * <li>{@link #isDone()} 恒为 true；{@link #isSuccess()} 由构造时的 success 决定</li>
	 * <li>不可取消：{@link #cancel(boolean)} 恒返回 false，{@link #isCancelled()} 恒为 false</li>
	 * <li>不支持补偿：{@link #isRollbackSupported()} 恒为 false，{@link #rollback()} 返回固定的失败信号</li>
	 * </ul>
	 * <p>
	 * 用途：短路编排、默认值占位、补偿流程的「无补偿」标记。
	 */
	@Getter
	@RequiredArgsConstructor
	public static class DoneSignal implements Signal {
		private static final Signal TRUE = new DoneSignal(true);
		private static final Signal FALSE = new DoneSignal(false);

		/** 是否成功 */
		private final boolean success;

		/**
		 * 无等待，直接返回。
		 */
		@Override
		public void await() throws InterruptedException {
		}

		/**
		 * 无等待，直接返回成功。
		 *
		 * @return true
		 */
		@Override
		public boolean await(long timeout, @NonNull TimeUnit unit) throws InterruptedException {
			return true;
		}

		/**
		 * 固定终态不支持取消。
		 *
		 * @return false
		 */
		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		/**
		 * 固定终态永不被取消。
		 *
		 * @return false
		 */
		@Override
		public boolean isCancelled() {
			return false;
		}

		/**
		 * 固定为已完成。
		 *
		 * @return true
		 */
		@Override
		public boolean isDone() {
			return true;
		}

		/**
		 * 固定终态不支持补偿。
		 *
		 * @return false
		 */
		@Override
		public boolean isRollbackSupported() {
			return false;
		}

		/**
		 * 固定终态永远不会处于「已回滚」。
		 *
		 * @return false
		 */
		@Override
		public boolean isRolledBack() {
			return false;
		}

		/**
		 * 固定终态无补偿动作，返回固定的失败信号占位。
		 *
		 * @return 固定失败信号
		 */
		@Override
		public Signal rollback() {
			return FALSE;
		}
	}

	/**
	 * 基于匹配谓词实现的组合模式模板。
	 *
	 * <p>
	 * 把 {@code isDone}、{@code isSuccess}、{@code isCancelled}、{@code isRollbackSupported}、
	 * {@code isRolledBack} 五类布尔状态统一委托给 {@link #match}；子类只需决定聚合语义
	 * （{@code allMatch} / {@code anyMatch} / 自定义 N-of-M）以及等待与取消策略。
	 * <p>
	 * 这样做的好处：新增组合算子时不必逐个重复实现状态方法，也不会漏实现某个状态导致语义不一致。
	 */
	public static abstract class MatchCompositePattern implements CompositePattern {
		/**
		 * 聚合取消状态：按当前模式的匹配规则（全部 / 任意）判定。
		 *
		 * @param elements 子信号集合
		 * @return 聚合后的取消状态
		 */
		public boolean isCancelled(@NonNull Streamable<? extends Signal> elements) {
			return match(elements, Signal::isCancelled);
		}

		/**
		 * 聚合完成状态。
		 *
		 * @param elements 子信号集合
		 * @return 聚合后的完成状态
		 */
		public boolean isDone(@NonNull Streamable<? extends Signal> elements) {
			return match(elements, Signal::isDone);
		}

		/**
		 * 聚合补偿能力：注意这是「能力」而非「状态」，All/Any 的语义差别见各模式注释。
		 *
		 * @param elements 子信号集合
		 * @return 聚合后的补偿能力判定
		 */
		public boolean isRollbackSupported(@NonNull Streamable<? extends Signal> elements) {
			return match(elements, Signal::isRollbackSupported);
		}

		/**
		 * 聚合补偿完成状态。
		 *
		 * @param elements 子信号集合
		 * @return 聚合后的「是否已完成回滚」判定
		 */
		public boolean isRolledBack(@NonNull Streamable<? extends Signal> elements) {
			return match(elements, Signal::isRolledBack);
		}

		/**
		 * 聚合成功状态。
		 *
		 * @param elements 子信号集合
		 * @return 聚合后的成功判定
		 */
		public boolean isSuccess(@NonNull Streamable<? extends Signal> elements) {
			return match(elements, Signal::isSuccess);
		}

		/**
		 * 匹配原语：由子类决定「全部满足」还是「任意满足」。
		 *
		 * @param elements  子信号集合
		 * @param predicate 状态谓词
		 * @param <E>       子信号类型
		 * @return 匹配结果
		 */
		public abstract <E extends Signal> boolean match(Streamable<? extends E> elements,
				Predicate<? super E> predicate);
	}

	/**
	 * Or 组合模式抽象基类：「任一满足」语义。
	 *
	 * <p>
	 * 状态聚合：任一子信号 {@code isDone} 即 done；任一 {@code isSuccess} 即 success；任一 {@code isCancelled} 即 cancelled；
	 * {@code isRollbackSupported} / {@code isRolledBack} 同理取任一。
	 *
	 * <p>
	 * 取消策略：依然向<b>全部</b>子信号广播 {@code cancel}，只是返回值用 anyMatch 聚合。
	 *
	 * <p>
	 * 需要留意的语义陷阱：
	 * <ul>
	 * <li>{@code isRollbackSupported} 取「任一支持」，只能说明「存在可回滚单元」，
	 * <b>不等于</b>「本次运行实际走到的那条分支可以回滚」</li>
	 * <li>正向「任一成功即满足」<b>不等于</b>「其余子信号已被取消或忽略」；取消与回滚动作始终广播到全部子节点</li>
	 * </ul>
	 */
	public static abstract class Or extends MatchCompositePattern {
		/**
		 * 向全部子信号广播取消，再对返回结果做 anyMatch 聚合。
		 *
		 * @param elements             子信号集合
		 * @param mayInterruptIfRunning 是否允许中断正在执行的线程
		 * @return true 表示至少有一个子信号被成功取消
		 */
		@Override
		public boolean cancel(@NonNull Streamable<? extends Signal> elements, boolean mayInterruptIfRunning) {
			// 取消所有的，只对结果进行判断
			return elements.mapPure((e) -> e.cancel(mayInterruptIfRunning)).toList().stream()
					.anyMatch(Boolean.TRUE::equals);
		}

		/**
		 * 任一匹配：谓词对任一子信号成立即返回 true。
		 *
		 * @param elements  子信号集合
		 * @param predicate 状态谓词
		 * @return 是否存在任一满足
		 */
		@Override
		public <E extends Signal> boolean match(Streamable<? extends E> elements, Predicate<? super E> predicate) {
			return elements.anyMatch(predicate);
		}
	}

	/**
	 * 并行 And 组合模式：全部子信号并行等待，全部完成才算 done，全部成功才算 success。
	 *
	 * <p>
	 * <b>超时策略（与 {@link SequentialAnd} 刻意不同）</b>：{@code timeout} 是<b>每个子信号各自的执行兜底上限</b>。
	 * 所有子信号并行起跑，每个都拿到完整时限、互不瓜分；外层
	 * {@link CompletableFuture#get(long, TimeUnit)} 再叠加一层等待侧兜底。
	 * 设计目标是防止某个子任务永久阻塞拖死线程池，实时性只是附带收益。
	 *
	 * <p>
	 * 语义边界：
	 * <ul>
	 * <li>返回 false 只代表<b>等待侧超时</b>，不会取消任何子信号；子信号继续运行，直到自身完成或自身兜底到期</li>
	 * <li>子信号自身兜底到期后会变为 done（失败态），但 And 需要<b>全部 done</b> 才满足，
	 * 因此整体完成时间由最慢的子信号决定</li>
	 * <li>等待线程被中断抛出 {@link InterruptedException}，同样不代表后台子任务被终止</li>
	 * </ul>
	 */
	@RequiredArgsConstructor
	@Getter
	public static class ParallelAnd extends And {
		/** 执行器，为 null 时使用 ForkJoinPool.commonPool */
		private final Executor executor;

		/**
		 * 并行等待所有子信号抵达终态（无超时）。
		 *
		 * <p>
		 * 已全部 done 时立即返回；否则为每个尚未完成的子信号提交一个等待任务，等待全部结束。
		 * <p>
		 * 无超时版本会无限等待：子信号若永不完成，等待线程与线程池线程都将被长期占用，
		 * 生产环境建议优先使用 {@link #await(Streamable, long, TimeUnit)}。
		 *
		 * @param elements 子信号集合
		 * @throws InterruptedException 等待线程被中断（不会终止后台子任务）
		 */
		@Override
		public void await(@NonNull Streamable<? extends Signal> elements) throws InterruptedException {
			List<? extends Signal> list = elements.toList();
			if (isDone(Streamable.of(list))) {
				return;
			}

			@SuppressWarnings("rawtypes")
			CompletableFuture[] completableFutures = list.stream().map((stage) -> {
				Runnable runnable = () -> {
					try {
						stage.await();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				};
				return executor == null ? CompletableFuture.runAsync(runnable)
						: CompletableFuture.runAsync(runnable, executor);
			}).toArray(CompletableFuture[]::new);
			CompletableFuture<?> completableFuture = CompletableFuture.allOf(completableFutures);
			try {
				completableFuture.get();
			} catch (ExecutionException e) {
			}
		}

		/**
		 * 带超时并行等待所有子信号完成。
		 *
		 * <p>
		 * {@code timeout} 同时充当：① 每个子信号的执行兜底上限；② 外层整体等待上限。
		 * 两者是独立的两层计时器，任一先到期都会结束等待。
		 *
		 * <p>
		 * 注意：返回 false 仅表示等待侧超时，<b>不会取消子信号</b>，子信号仍会在后台跑完或抵达自身兜底。
		 *
		 * @param elements 子信号集合
		 * @param timeout  单节点执行兜底时长 / 等待侧最大等待时长
		 * @param unit     时间单位
		 * @return true：全部子信号在时限内完成；false：等待侧超时（子信号不受影响）
		 * @throws InterruptedException 等待线程被中断（不会终止后台子任务）
		 */
		@Override
		public boolean await(@NonNull Streamable<? extends Signal> elements, long timeout, TimeUnit unit)
				throws InterruptedException {
			List<? extends Signal> list = elements.toList();
			if (isDone(Streamable.of(list))) {
				return true;
			}

			@SuppressWarnings("rawtypes")
			CompletableFuture[] completableFutures = list.stream().map((stage) -> {
				Runnable runnable = () -> {
					try {
						stage.await(timeout, unit);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				};
				return executor == null ? CompletableFuture.runAsync(runnable)
						: CompletableFuture.runAsync(runnable, executor);
			}).toArray(CompletableFuture[]::new);
			CompletableFuture<?> completableFuture = CompletableFuture.allOf(completableFutures);
			try {
				completableFuture.get(timeout, unit);
			} catch (ExecutionException e) {
			} catch (TimeoutException e) {
				return false;
			}
			return true;
		}
	}

	/**
	 * 并行 Or 组合模式：全部子信号并行等待，任一完成即 done，任一成功即 success。
	 *
	 * <p>
	 * <b>超时策略（与 {@link SequentialAnd} 刻意不同）</b>：{@code timeout} 是<b>每个子信号各自的执行兜底上限</b>，
	 * 各子信号并行起跑、获得完整时限、互不瓜分；外层 {@link CompletableFuture#get(long, TimeUnit)} 叠加等待侧兜底。
	 *
	 * <p>
	 * 语义边界（最容易误解的一处）：
	 * <ul>
	 * <li>任一子信号完成，等待即返回；<b>其余子信号不会被自动取消</b>，仍会继续运行直到自身完成或自身兜底到期</li>
	 * <li>因此「Or 已满足」≠「剩余任务已停止」；要真正停止，须显式调用 {@link #cancel(boolean)}</li>
	 * <li>返回 false 仍只代表等待侧超时，不代表子信号被终止</li>
	 * </ul>
	 */
	@RequiredArgsConstructor
	@Getter
	public static class ParallelOr extends Or {
		private static final ParallelOr INSTANCE = new ParallelOr(null);
		/** 执行器，为 null 时使用 ForkJoinPool.commonPool */
		private final Executor executor;

		/**
		 * 并行等待，任一子信号完成即返回。
		 *
		 * <p>
		 * 已满足 Or 条件时立即返回；否则为每个尚未完成的子信号提交等待任务，任意一个结束即返回。
		 * <p>
		 * 无超时版本会无限等待首个完成的子信号；返回后<b>其余子信号仍在后台运行，且不会被自动取消</b>。
		 *
		 * @param elements 子信号集合
		 * @throws InterruptedException 等待线程被中断（不会终止后台子任务）
		 */
		@Override
		public void await(@NonNull Streamable<? extends Signal> elements) throws InterruptedException {
			List<? extends Signal> list = elements.toList();
			if (isDone(Streamable.of(list))) {
				return;
			}

			@SuppressWarnings("rawtypes")
			CompletableFuture[] completableFutures = list.stream().map((stage) -> {
				Runnable runnable = () -> {
					try {
						stage.await();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				};
				return executor == null ? CompletableFuture.runAsync(runnable)
						: CompletableFuture.runAsync(runnable, executor);
			}).toArray(CompletableFuture[]::new);
			CompletableFuture<?> completableFuture = CompletableFuture.anyOf(completableFutures);
			try {
				completableFuture.get();
			} catch (ExecutionException e) {
			}
		}

		/**
		 * 带超时并行等待，任一子信号完成即提前返回；全部子信号都未在时限内完成则返回 false。
		 *
		 * <p>
		 * {@code timeout} 同时充当：① 每个子信号的执行兜底上限；② 外层整体等待上限。
		 * 返回时其余子信号依然在后台运行，不会被自动取消。
		 *
		 * @param elements 子信号集合
		 * @param timeout  单节点执行兜底时长 / 等待侧最大等待时长
		 * @param unit     时间单位
		 * @return true：有子信号在时限内完成；false：等待侧超时（子信号不受影响）
		 * @throws InterruptedException 等待线程被中断（不会终止后台子任务）
		 */
		@Override
		public boolean await(@NonNull Streamable<? extends Signal> elements, long timeout, TimeUnit unit)
				throws InterruptedException {
			List<? extends Signal> list = elements.toList();
			if (isDone(Streamable.of(list))) {
				return true;
			}

			@SuppressWarnings("rawtypes")
			CompletableFuture[] completableFutures = list.stream().map((stage) -> {
				Runnable runnable = () -> {
					try {
						stage.await(timeout, unit);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				};
				return executor == null ? CompletableFuture.runAsync(runnable)
						: CompletableFuture.runAsync(runnable, executor);
			}).toArray(CompletableFuture[]::new);
			CompletableFuture<?> completableFuture = CompletableFuture.anyOf(completableFutures);
			try {
				completableFuture.get(timeout, unit);
			} catch (ExecutionException e) {
			} catch (TimeoutException e) {
				return false;
			}
			return true;
		}
	}

	/**
	 * 串行 And 组合模式：按顺序依次等待子信号，全部完成才算 done，全部成功才算 success。
	 *
	 * <p>
	 * <b>超时策略（与并行模式刻意不同）</b>：{@code timeout} 是<b>整条链路的总时间预算</b>。
	 * 每个子信号只拿到「剩余时间」（deadline - 已消耗时间）；剩余时间不足时立即返回 false，不再等待后续节点。
	 * 设计目标是以一份总预算换取链路整体的<b>实时可控</b>，而不是给每个节点独立配额。
	 */
	public static class SequentialAnd extends And {
		private static final SequentialAnd INSTANCE = new SequentialAnd();

		/**
		 * 串行依次等待每个子信号完成，已完成（done）的子信号直接跳过。
		 *
		 * <p>
		 * 无超时版本：任一子信号永不完成会阻塞整条链路。
		 *
		 * @param elements 子信号集合
		 * @throws InterruptedException 等待线程被中断
		 */
		@Override
		public void await(@NonNull Streamable<? extends Signal> elements) throws InterruptedException {
			try (Stream<? extends Signal> stream = elements.stream()) {
				Iterator<? extends Signal> iterator = stream.iterator();
				while (iterator.hasNext()) {
					Signal element = iterator.next();
					if (element.isDone()) {
						continue;
					}
					element.await();
				}
			}
		}

		/**
		 * 带超时串行等待。
		 *
		 * <p>
		 * {@code timeout} 是整条链路总预算，按 deadline 逐节点递减；某个子信号耗尽剩余时间后立即返回 false，
		 * 不再继续等待后续节点。已完成的子信号不消耗预算。
		 *
		 * <p>
		 * 注意：这里的 timeout <b>不会</b>分别下发给每个子信号，与并行模式语义不同。
		 *
		 * @param elements 子信号集合
		 * @param timeout  链路总超时时长
		 * @param unit     时间单位
		 * @return true：全部子信号在总预算内完成；false：总预算耗尽
		 * @throws InterruptedException 等待线程被中断
		 */
		@Override
		public boolean await(@NonNull Streamable<? extends Signal> elements, long timeout, TimeUnit unit)
				throws InterruptedException {
			long deadline = System.nanoTime() + unit.toNanos(timeout);
			try (Stream<? extends Signal> stream = elements.stream()) {
				Iterator<? extends Signal> iterator = stream.iterator();
				while (iterator.hasNext()) {
					Signal element = iterator.next();
					if (element.isDone()) {
						continue;
					}

					long t = deadline - System.nanoTime();
					if (t < 0) {
						return false;
					}

					if (!element.await(t, TimeUnit.NANOSECONDS)) {
						return false;
					}
				}
			}
			return true;
		}
	}

	/**
	 * 静态工厂：批量构造串行 And 组合信号，等价于 {@code composite(SequentialAnd.INSTANCE, elements)}。
	 *
	 * @param elements 子信号流
	 * @param <E>      子信号类型
	 * @return 串行 And 组合信号
	 */
	static <E extends Signal> Signals<E> batch(@NonNull Streamable<E> elements) {
		return composite(SequentialAnd.INSTANCE, elements);
	}

	/**
	 * 静态工厂：使用指定组合模式构造组合信号容器。
	 *
	 * <p>
	 * 返回的是<b>无状态实时视图</b>：不缓存子信号状态，每次查询都重新遍历 {@code elements} 聚合，
	 * 因此传入的 {@code Streamable} 应尽量是低成本、可重复遍历的（如 list 视图）。
	 *
	 * @param compositePattern 组合规则 And / Or 及其具体实现
	 * @param elements         子信号流
	 * @param <E>              子信号类型
	 * @return 组合信号
	 */
	static <E extends Signal> Signals<E> composite(@NonNull CompositePattern compositePattern,
			@NonNull Streamable<E> elements) {
		return new CompositeSignals<>(compositePattern, elements);
	}

	/**
	 * 构造一个固定终态信号常量。
	 *
	 * @param success true=成功信号；false=失败信号
	 * @return 固定 DoneSignal（不可取消、不支持补偿）
	 */
	public static Signal done(boolean success) {
		return success ? DoneSignal.TRUE : DoneSignal.FALSE;
	}

	/**
	 * 串行 And 组合：先等待当前信号，再依次等待 {@code others}。
	 *
	 * <p>
	 * 返回的组合信号同样实现 {@link Signal}，可继续参与外层编排；若外层仍是串行 And，会自动拍平为一层。
	 *
	 * @param others 其他信号
	 * @return 串行 And 组合信号
	 */
	default Signal and(@NonNull Signal... others) {
		return composite(SequentialAnd.INSTANCE, others);
	}

	/**
	 * 阻塞等待至信号抵达终态（无超时）。
	 *
	 * <p>
	 * 只响应中断，业务异常与取消信息不外抛，通过 {@link #isSuccess()}、{@link #isCancelled()} 等状态方法查询。
	 * <p>
	 * 注意：本方法<b>不等价于</b> {@code Future#get()}，不会抛出业务异常；
	 * 业务失败应通过 {@link #isSuccess()} 判断，异常信息由实现类附加接口提供。
	 *
	 * @throws InterruptedException 等待线程被中断
	 */
	void await() throws InterruptedException;

	/**
	 * 带超时阻塞等待至信号抵达终态。
	 *
	 * <p>
	 * 返回 true：在时限内抵达终态（成功 / 失败 / 取消都算）；返回 false：<b>仅表示等待超时</b>。
	 * <p>
	 * 超时不代表任务被取消或终止，具体超时预算的归属（链路总预算 / 单节点兜底）由实现模式决定。
	 *
	 * @param timeout 超时时长
	 * @param unit    时间单位
	 * @return true：超时前抵达终态；false：等待超时
	 * @throws InterruptedException 等待线程被中断
	 */
	boolean await(long timeout, @NonNull TimeUnit unit) throws InterruptedException;

	/**
	 * 尝试取消执行，语义对齐 {@link java.util.concurrent.Future#cancel(boolean)}。
	 *
	 * <p>
	 * 对组合信号调用时，取消动作会<b>广播给全部子信号</b>，返回值按 And（全成功）或 Or（任一成功）聚合。
	 *
	 * @param mayInterruptIfRunning 是否允许中断正在执行的线程
	 * @return true：本次调用成功将目标置为取消态；false：已完成 / 已取消 / 无法取消
	 */
	boolean cancel(boolean mayInterruptIfRunning);

	/**
	 * 使用指定组合模式，将当前信号与 {@code others} 合并为组合信号。
	 *
	 * @param compositePattern 组合规则 And / Or 及其具体实现
	 * @param others           其他信号
	 * @return 组合信号
	 */
	default Signal composite(@NonNull CompositePattern compositePattern, @NonNull Signal... others) {
		return new CompositeSignals<>(compositePattern, Streamable.array(this).concat(others));
	}

	/**
	 * 是否已被成功取消。
	 *
	 * <p>
	 * 取消属于终态：返回 true 时 {@link #isDone()} 必为 true。语义与 {@code Future#isCancelled()} 一致。
	 *
	 * @return true：已被取消
	 */
	boolean isCancelled();

	/**
	 * 是否抵达终态。
	 *
	 * @return true：成功、失败、取消均算完成；false：运行中
	 */
	boolean isDone();

	/**
	 * 是否具备补偿能力（能力维度，不代表已经执行过回滚）。
	 *
	 * <p>
	 * 组合信号按模式聚合：And 取「全部支持」，Or 取「任一支持」。
	 * 注意 Or 的「任一支持」只表示存在可回滚单元，不代表实际跑到的分支一定可回滚。
	 *
	 * @return true：支持回滚
	 */
	boolean isRollbackSupported();

	/**
	 * 补偿是否已执行完成（状态维度）。
	 *
	 * <p>
	 * 与 {@link #isRollbackSupported()} 是两件事：前者回答「能不能回滚」，本方法回答「是否已经回滚完了」。
	 * 调用 {@link #rollback()} 只是构造补偿信号，本方法要等补偿信号执行完成后才会转为 true。
	 *
	 * @return true：回滚已完成
	 */
	boolean isRolledBack();

	/**
	 * 是否正常成功完成。
	 *
	 * @return true：正常成功；false：未完成 / 失败 / 取消
	 */
	boolean isSuccess();

	/**
	 * 并行 Or 组合：当前信号与 {@code others} 并行等待，任一完成即整体完成。
	 *
	 * <p>
	 * 返回的组合信号同样实现 {@link Signal}，可继续参与外层编排；若外层仍是并行 Or，会自动拍平为一层。
	 *
	 * @param others 其他信号
	 * @return 并行 Or 组合信号
	 */
	default Signal or(@NonNull Signal... others) {
		return composite(ParallelOr.INSTANCE, others);
	}

	/**
	 * 构造本次回滚（补偿）流程的信号。
	 *
	 * <p>
	 * <b>这是表达式构造，不是执行命令</b>：调用只返回一个代表补偿流程的 {@link Signal}，
	 * 不会自动执行、也不会阻塞；要执行补偿，须对返回信号调用 {@code await()}。
	 *
	 * <p>
	 * 组合信号的行为：对<b>全部</b>子信号逐个调用 {@code rollback()}，再沿用同一组合模式聚合补偿信号
	 * （即补偿拓扑与正向编排拓扑保持同构，Or 也不会只补偿胜出分支）。
	 *
	 * <p>
	 * 每次调用都会生成独立的补偿信号；框架不记录「是否已发起过回滚」，重复调用不会被拦截，
	 * 补偿幂等由叶子实现自行保证。
	 *
	 * @return 代表本次补偿流程的信号
	 */
	Signal rollback();

	/**
	 * 同步等待至终态，捕获并恢复中断标记后返回自身，便于链式书写。
	 *
	 * <p>
	 * 注意：吞掉中断只是让调用方继续往下走，<b>不会终止</b>任何后台子任务。
	 *
	 * @return this
	 */
	default Signal sync() {
		try {
			await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return this;
	}
	
	/**
	 * 包装为单元素组合信号（串行 And），便于统一按「组合信号」处理（例如统一读取子信号集合）。
	 * <p>
	 * boxed：叶子 Signal → 包装成 Signals（单元素组合容器），类型向上对齐；
	 * 本默认实现直接返回自身，子类 {@link Signals} 重写为真实包装逻辑。
	 * </p>
	 * @return 单元素组合信号
	 */
	default Signal boxed() {
	    return this;
	}

	/**
	 * 解包：将当前叶子信号包装为单元素 {@link Signals} 容器，方便统一用组合容器API遍历子元素、读取pattern。
	 * <p>
	 * 与 boxed 配对：
	 * <ul>
	 * <li>boxed：偏向 Signal 侧，返回 Signal 类型，统一编排入口</li>
	 * <li>unboxed：偏向容器侧，返回 Signals 强类型，拿到 getElements / getCompositePattern</li>
	 * </ul>
	 * 包装后是一个只包含自己的串行And单元素组合视图，不会改变原有状态、等待、取消、回滚行为。
	 * @return 承载当前信号的单元素组合容器
	 */
	default Signals<? extends Signal> unboxed(){
	    return new CompositeSignals<>(SequentialAnd.INSTANCE, Streamable.singleton(this));
	}

}
