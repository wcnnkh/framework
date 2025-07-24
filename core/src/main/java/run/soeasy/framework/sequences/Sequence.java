package run.soeasy.framework.sequences;

import java.util.function.Function;

import lombok.NonNull;

/**
 * 序列生成器接口，定义生成连续或唯一序列的统一标准， 支持泛型以生成不同类型的序列值（如数字、字符串、自定义对象等）。
 *
 * <p>
 * <b>核心功能：</b>
 * <ul>
 * <li>序列生成：通过{@link #next()}方法获取下一个序列值</li>
 * <li>泛型支持：支持任意类型的序列值生成，满足多样化业务场景</li>
 * <li>唯一性保证：实现类应确保生成的序列值具有逻辑连续性或全局唯一性</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>ID生成：生成数据库主键、分布式唯一标识等</li>
 * <li>有序序列：生成递增/递减的数值序列、时间戳序列</li>
 * <li>迭代器基础：作为集合遍历、数据分片的基础生成接口</li>
 * <li>业务编号：生成订单号、流水号等具有业务含义的序列</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <T> 序列值的类型，实现类需保证其可序列化为唯一标识
 */
@FunctionalInterface
public interface Sequence<T> {
	/**
	 * 获取序列的下一个值（核心方法）。
	 * <p>
	 * 实现类必须保证：
	 * <ul>
	 * <li>多次调用返回的序列值具有可预测的顺序性</li>
	 * <li>在指定上下文内具有唯一性（如分布式环境下的全局唯一）</li>
	 * <li>超出资源限制时抛出{@link UnsupportedOperationException}</li>
	 * </ul>
	 * 
	 * @return 下一个序列值，不可为null
	 * @throws UnsupportedOperationException 当无法生成有效序列值时抛出（如资源耗尽）
	 */
	@NonNull
	T next() throws UnsupportedOperationException;

	default <R> Sequence<R> map(@NonNull Function<? super T, ? extends R> mapper) {
		return new MappedSequence<>(this, mapper);
	}
}