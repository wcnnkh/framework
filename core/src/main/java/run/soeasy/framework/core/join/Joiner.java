package run.soeasy.framework.core.join;

import java.io.IOException;
import java.util.Iterator;

import lombok.NonNull;

/**
 * 元素追加器接口 定义将元素追加到目标位置的功能，支持单个元素追加和批量元素追加
 * 
 * 该接口设计用于高效地将多个元素连接成一个连续的输出， 适用于日志记录、数据拼接、文本生成等场景
 * 
 * @param <E> 待追加的元素类型
 * @author soeasy.run
 */
@FunctionalInterface
public interface Joiner<E> {
	/**
	 * 将单个元素追加到目标位置
	 * 
	 * 核心追加方法，实现类需定义元素的具体追加逻辑
	 * 
	 * @param joinable 目标追加位置（如StringBuilder、FileWriter等）
	 * @param count    已成功追加的元素数量（用于统计和位置计算）
	 * @param element  待追加的元素
	 * @return 追加成功后的总元素数量
	 * @throws IOException 当向目标位置写入数据失败时抛出
	 */
	long join(@NonNull Appendable joinable, long count, E element) throws IOException;

	/**
	 * 批量追加元素到目标位置（迭代器版本）
	 * 
	 * 遍历迭代器中的所有元素，逐个调用{@link #join(Appendable, long, Object)}方法
	 * 
	 * @param <S>      元素类型
	 * @param joinable 目标追加位置
	 * @param iterator 元素迭代器
	 * @param joiner   追加器实现
	 * @return 成功追加的元素总数
	 * @throws IOException         当任意元素追加失败时抛出
	 * @throws ArithmeticException 当元素数量超过long最大值时抛出
	 */
	public static <S> long joinAll(@NonNull Appendable joinable, @NonNull Iterator<? extends S> iterator,
			@NonNull Joiner<? super S> joiner) throws IOException, ArithmeticException {
		long count = 0;
		while (iterator.hasNext()) {
			// 使用Math.addExact防止整数溢出
			count = Math.addExact(joiner.join(joinable, count, iterator.next()), count);
		}
		return count;
	}

	/**
	 * 批量追加元素到StringBuilder（迭代器版本）
	 * 
	 * 便捷方法，内部调用{@link #joinAll(Appendable, Iterator, Joiner)}
	 * 
	 * @param <S>      元素类型
	 * @param joinable StringBuilder目标
	 * @param iterator 元素迭代器
	 * @param joiner   追加器实现
	 * @return 成功追加的元素总数
	 * @throws ArithmeticException 当元素数量超过long最大值时抛出
	 */
	public static <S> long joinAll(@NonNull StringBuilder joinable, @NonNull Iterator<? extends S> iterator,
			@NonNull Joiner<? super S> joiner) throws ArithmeticException {
		try {
			return joinAll((Appendable) joinable, iterator, joiner);
		} catch (IOException e) {
			// StringBuilder不会抛出IOException，此处作为防御性处理
			throw new IllegalStateException("Unexpected IOException in StringBuilder join", e);
		}
	}

	/**
	 * 批量追加元素到目标位置（可迭代对象版本）
	 * 
	 * 内部将Iterable转换为Iterator，再调用{@link #joinAll(Appendable, Iterator, Joiner)}
	 * 
	 * @param <S>      元素类型
	 * @param joinable 目标追加位置
	 * @param iterable 元素可迭代对象
	 * @param joiner   追加器实现
	 * @return 成功追加的元素总数
	 * @throws IOException         当任意元素追加失败时抛出
	 * @throws ArithmeticException 当元素数量超过long最大值时抛出
	 */
	public static <S> long joinAll(@NonNull Appendable joinable, @NonNull Iterable<? extends S> iterable,
			@NonNull Joiner<? super S> joiner) throws IOException, ArithmeticException {
		return joinAll(joinable, iterable.iterator(), joiner);
	}

	/**
	 * 批量追加元素到StringBuilder（可迭代对象版本）
	 * 
	 * 便捷方法，内部将Iterable转换为Iterator，再调用相应方法
	 * 
	 * @param <S>      元素类型
	 * @param joinable StringBuilder目标
	 * @param iterable 元素可迭代对象
	 * @param joiner   追加器实现
	 * @return 成功追加的元素总数
	 * @throws ArithmeticException 当元素数量超过long最大值时抛出
	 */
	public static <S> long joinAll(@NonNull StringBuilder joinable, @NonNull Iterable<? extends S> iterable,
			@NonNull Joiner<? super S> joiner) throws ArithmeticException {
		return joinAll(joinable, iterable.iterator(), joiner);
	}

	/**
	 * 构建字符串（迭代器版本）
	 * 
	 * 便捷方法，创建StringBuilder并调用{@link #joinAll(StringBuilder, Iterator, Joiner)}
	 * 
	 * @param <S>      元素类型
	 * @param iterator 元素迭代器
	 * @param joiner   追加器实现
	 * @return 包含所有追加元素的StringBuilder
	 */
	public static <S> StringBuilder buildString(@NonNull Iterator<? extends S> iterator,
			@NonNull Joiner<? super S> joiner) {
		StringBuilder builder = new StringBuilder();
		joinAll(builder, iterator, joiner);
		return builder;
	}

	/**
	 * 构建字符串（可迭代对象版本）
	 * 
	 * 便捷方法，内部将Iterable转换为Iterator，再调用{@link #buildString(Iterator, Joiner)}
	 * 
	 * @param <S>      元素类型
	 * @param iterable 元素可迭代对象
	 * @param joiner   追加器实现
	 * @return 包含所有追加元素的StringBuilder
	 */
	public static <S> StringBuilder buildString(@NonNull Iterable<? extends S> iterable,
			@NonNull Joiner<? super S> joiner) {
		return buildString(iterable.iterator(), joiner);
	}
}
