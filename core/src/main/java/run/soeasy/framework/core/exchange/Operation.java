package run.soeasy.framework.core.exchange;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.CompositeOperation.Mode;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 通用操作生命周期管理接口
 * <p>
 * 定义操作的核心状态查询、行为控制、同步等待及便捷创建规范，统一完成/取消/回滚/成功的判定语义。
 *
 * @author soeasy.run
 */
public interface Operation {
	/**
	 * 无回滚的成功操作常量（不可变、线程安全） 语义：操作成功完成、不支持回滚、未取消。
	 */
	Operation SUCCESS = success();

	// ========== 核心状态/操作方法 ==========

	/**
	 * 尝试取消操作
	 * 
	 * @return true=取消成功（操作可取消且首次取消）；false=操作不可取消（已完成/已取消）
	 */
	boolean cancel();

	/**
	 * 尝试回滚操作（仅成功完成的操作可回滚）
	 * 
	 * @return true=回滚成功；false=不满足回滚条件或回滚逻辑执行失败
	 */
	boolean rollback();

	/**
	 * 判断操作是否可取消（未完成且未取消）
	 * 
	 * @return true=可取消；false=不可取消
	 */
	boolean isCancellable();

	/**
	 * 判断操作是否已取消
	 * 
	 * @return true=已取消；false=未取消
	 */
	boolean isCancelled();

	/**
	 * 判断操作是否支持回滚
	 * 
	 * @return true=支持回滚；false=无回滚逻辑
	 */
	boolean isRollbackSupported();

	/**
	 * 判断操作是否已回滚
	 * 
	 * @return true=已回滚；false=未回滚
	 */
	boolean isRollback();

	/**
	 * 判断操作是否完成（正常结束/取消/超时均视为完成）
	 * 
	 * @return true=已完成；false=未完成
	 */
	boolean isDone();

	/**
	 * 判断操作是否最终成功（完成、未取消、未回滚、无失败原因）
	 * 
	 * @return true=成功；false=失败/取消/回滚/未完成
	 */
	boolean isSuccess();

	/**
	 * 判断操作是否失败
	 * 
	 * @return true=成功；false=失败
	 */
	default boolean isFailure() {
		return isDone() && !isSuccess();
	}

	/**
	 * 获取操作失败原因
	 * 
	 * @return 失败原因（成功操作返回null）
	 */
	Throwable cause();

	// ========== 底层等待方法 ==========

	/**
	 * 无限阻塞等待操作完成
	 * 
	 * @throws InterruptedException 等待被中断时抛出
	 */
	void await() throws InterruptedException;

	/**
	 * 带超时阻塞等待操作完成
	 * 
	 * @param timeout 超时时间（非负，0表示立即检查）
	 * @param unit    时间单位（非null）
	 * @return true=超时前完成；false=超时未完成
	 * @throws InterruptedException     等待被中断时抛出
	 * @throws IllegalArgumentException 超时时间为负时抛出
	 */
	boolean await(long timeout, TimeUnit unit) throws InterruptedException;

	// ========== 便捷同步方法 ==========

	/**
	 * 便捷同步等待操作完成（自动处理中断异常）
	 * 
	 * @return 操作实例：正常完成返回自身；中断返回失败操作（保留中断状态）
	 */
	default Operation sync() {
		try {
			await();
			return this;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return Operation.failure(e);
		}
	}

	/**
	 * 带超时的便捷同步等待（自动处理中断/超时）
	 * 
	 * @param timeout 超时时间（非负，0表示立即检查）
	 * @param unit    时间单位（非null）
	 * @return 操作实例：完成返回自身；超时/中断返回失败操作
	 */
	default Operation sync(long timeout, TimeUnit unit) {
		try {
			boolean completed = await(timeout, unit);
			if (completed) {
				return this;
			}
			TimeoutException timeoutEx = new TimeoutException(
					String.format("Operation await timeout after %d %s", timeout, unit));
			return Operation.failure(timeoutEx);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return Operation.failure(e);
		}
	}

	// ========== 静态创建方法 ==========

	/**
	 * 创建支持回滚的成功操作
	 * 
	 * @param rollbackLogic 回滚逻辑（null表示不支持回滚）
	 * @return 成功操作实例（已转换为SUCCESS状态）
	 */
	public static Operation success(BooleanSupplier rollbackLogic) {
		Stage stage = new Stage(rollbackLogic);
		stage.trySuccess(); // 直接转换为SUCCESS状态
		return stage;
	}

	/**
	 * 创建无回滚的成功操作
	 * 
	 * @return 成功操作实例（已转换为SUCCESS状态）
	 */
	public static Operation success() {
		return success(null); // 复用上面的方法，简化逻辑
	}

	/**
	 * 创建失败操作
	 * 
	 * @param cause 失败原因（非null）
	 * @return 失败操作实例（已转换为FAILURE状态）
	 */
	public static Operation failure(Throwable cause) {
		Stage stage = new Stage();
		stage.tryFailure(cause); // 直接转换为FAILURE状态（复用非空校验）
		return stage;
	}

	/**
	 * 批量处理元素并创建批量操作
	 * 
	 * @param <S>       元素类型
	 * @param elements  待处理元素流（非null）
	 * @param mode      批量组合模式（AND/OR，非null）
	 * @param processor 元素转操作的处理器（非null，异常自动转为失败操作）
	 * @return 批量操作实例
	 */
	static <S> Operation batch(@NonNull Streamable<? extends S> elements, @NonNull Mode mode,
			@NonNull ThrowingFunction<? super S, ? extends Operation, ? extends Throwable> processor) {
		List<Operation> operations = new ArrayList<>();
		elements.forEach(element -> {
			Operation operation;
			try {
				operation = processor.apply(element);
			} catch (Throwable e) {
				operation = failure(e);
			}
			operations.add(operation);
		});
		return new CompositeOperation(mode, operations);
	}
}