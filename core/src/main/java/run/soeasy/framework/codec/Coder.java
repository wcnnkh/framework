package run.soeasy.framework.codec;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.comparator.Evaluator;
import run.soeasy.framework.core.comparator.Comparison;

/**
 * 单向数据转换器接口，集成了 {@link Function} 和 {@link Evaluator} 的能力，
 * 提供数据转换、结果校验、链式组合、闭环组合以及恒等转换器能力。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li><b>函数式转换</b>：继承 {@link Function}，可通过 {@link #apply(Object)} 执行转换， 支持
 * Lambda 表达式创建实例</li>
 * <li><b>结果校验</b>：继承 {@link Evaluator}，通过 {@link #check(Object, Object)} 将
 * 源数据转换后与期望值进行比较，适用于数据验证和断言场景</li>
 * <li><b>链式组合</b>：通过 {@link #from(Coder)} 和 {@link #to(Coder)} 进行前置/后置 转换拼接，等价于
 * {@link #compose(Function)} 和 {@link #andThen(Function)} 的语义化别名</li>
 * <li><b>类型连贯</b>：覆盖 {@code Function} 的 {@link #compose(Function)} 和
 * {@link #andThen(Function)} 方法，使其返回 {@code Coder} 类型，保证链式调用中类型不丢失</li>
 * <li><b>闭环组合</b>：通过 {@link #toUnary(Function)} 将当前转换器与反向函数组合， 转换为同类型的
 * {@link UnaryCoder}</li>
 * <li><b>恒等转换器</b>：通过 {@link #identity()} 获取不做任何转换的 {@link UnaryCoder} 单例</li>
 * </ul>
 *
 * <p>
 * <b>与 {@link Codec} 的关系：</b>
 * <ul>
 * <li>{@code Coder} 是单向转换器，代表编码或解码中的<b>单一方向</b></li>
 * <li>{@code Codec} 是双向编解码器，内部通过组合两个 {@code Coder}（{@link Codec#encoder()} 和
 * {@link Codec#decoder()}）实现双向转换</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>数据格式转换：如字符串与字节数组、对象与 JSON 的单向映射</li>
 * <li>数据校验：编码结果与期望值比对，用于测试和断言</li>
 * <li>转换链路拼接：多个转换步骤串联为统一的转换单元</li>
 * <li>作为 {@link Codec} 的构建块：为编解码器提供具体的转换实现</li>
 * </ul>
 *
 * <p>
 * <b>使用示例：</b>
 *
 * <pre>{@code
 * // 创建 Coder（Lambda 方式）
 * Coder<byte[], String> base64Coder = data -> Base64.getEncoder().encodeToString(data);
 *
 * // 直接转换
 * String result = base64Coder.apply(inputBytes);
 *
 * // 结果校验
 * Comparison comparison = base64Coder.check(inputBytes, expectedString);
 * if (comparison.isMatch()) { ... }
 *
 * // 链式组合
 * Coder<String, String> prefixedCoder = base64Coder.from(String::getBytes);
 * String output = prefixedCoder.apply("hello"); // "hello" → byte[] → base64
 *
 * // 闭环组合：编码后通过 mapper 映射回源类型
 * UnaryCoder<byte[]> roundTrip = base64Coder.toUnary(bytes -> Base64.getDecoder().decode(bytes));
 *
 * // 恒等转换器
 * UnaryCoder<String> identity = Coder.identity();
 *
 * // 当 Function 使用
 * stream.map(base64Coder).collect(Collectors.toList());
 * }</pre>
 *
 * @author soeasy.run
 * @param <T> 源数据类型（转换前类型）
 * @param <R> 目标数据类型（转换后类型）
 * @see Codec
 * @see UnaryCoder
 * @see Function
 * @see Evaluator
 */
@FunctionalInterface
public interface Coder<T, R> extends Function<T, R>, Evaluator<T, R> {

	/**
	 * 返回同类型恒等转换器单例。
	 * <p>
	 * 该转换器的 {@link #apply(Object)} 直接返回输入值本身， 即 {@code apply(x) == x}。
	 *
	 * <p>
	 * 恒等转换器在链式组合中充当中性元素：
	 * 
	 * <pre>{@code
	 * coder.from(Coder.identity()) == coder
	 * coder.to(Coder.identity()) == coder
	 * }</pre>
	 *
	 * @param <E> 数据类型
	 * @return 恒等转换器单例，不可为 null
	 */
	@SuppressWarnings("unchecked")
	public static <E> UnaryCoder<E> identity() {
		return (UnaryCoder<E>) IdentityCoder.INSTANCE;
	}

	/**
	 * 校验源数据经转换后是否与期望值匹配。
	 * <p>
	 * 默认实现：将 {@code left} 通过 {@link #apply(Object)} 转换后， 与 {@code right} 进行
	 * {@link Comparison#of(Object, Object)} 比较。
	 *
	 * @param left  源数据（待转换的值）
	 * @param right 期望值（与转换结果进行比较）
	 * @return 比较结果，包含匹配状态和详细信息
	 */
	@Override
	default @NonNull Comparison evaluate(T left, R right) {
		return Comparison.of(apply(left), right);
	}

	/**
	 * 组合前置转换器，形成新的转换器（语义化别名，等价于 {@link #compose(Function)}）。
	 * <p>
	 * 新转换器的转换流程为：
	 *
	 * <pre>
	 * F → [前置转换器] → T → [当前转换器] → R
	 * </pre>
	 *
	 * @param <E>  前置转换器的源类型
	 * @param from 前置转换器，不可为 null
	 * @return 组合后的新转换器
	 */
	default <E> Coder<E, R> from(@NonNull Coder<E, T> from) {
		return compose(from);
	}

	/**
	 * 组合后置转换器，形成新的转换器（语义化别名，等价于 {@link #andThen(Function)}）。
	 * <p>
	 * 新转换器的转换流程为：
	 *
	 * <pre>
	 * T → [当前转换器] → R → [后置转换器] → E
	 * </pre>
	 *
	 * @param <E> 后置转换器的目标类型
	 * @param to  后置转换器，不可为 null
	 * @return 组合后的新转换器
	 */
	default <E> Coder<T, E> to(@NonNull Coder<R, E> to) {
		return andThen(to);
	}

	/**
	 * 返回一个组合转换器，该转换器先将输入传递给 {@code before} 函数， 再将其结果传递给当前转换器。
	 * <p>
	 * 覆盖 {@link Function#compose(Function)} 以返回 {@code Coder} 类型， 确保链式调用中类型信息不丢失。
	 *
	 * @param <V>    输入到 {@code before} 函数的类型
	 * @param before 在调用当前转换器之前应用的函数，不可为 null
	 * @return 组合后的新转换器
	 */
	@Override
	default <V> Coder<V, R> compose(@NonNull Function<? super V, ? extends T> before) {
		return (s) -> Coder.this.apply(before.apply(s));
	}

	/**
	 * 返回一个组合转换器，该转换器先将输入通过当前转换器， 再将其结果传递给 {@code after} 函数。
	 * <p>
	 * 覆盖 {@link Function#andThen(Function)} 以返回 {@code Coder} 类型， 确保链式调用中类型信息不丢失。
	 *
	 * @param <V>   {@code after} 函数返回的结果类型
	 * @param after 当前转换器执行后应用的函数，不可为 null
	 * @return 组合后的新转换器
	 */
	@Override
	default <V> Coder<T, V> andThen(@NonNull Function<? super R, ? extends V> after) {
		return (s) -> after.apply(Coder.this.apply(s));
	}

	/**
	 * 将当前 Coder 与反向函数组合，转换为同类型的 {@link UnaryCoder}（T↔T 闭环转换器）。
	 * <p>
	 * 组合后的 {@code UnaryCoder} 的转换流程为 T → T 的闭环：
	 *
	 * <pre>
	 * T → [当前 Coder] → R → [mapper] → T
	 * </pre>
	 *
	 * <p>
	 * 典型用途是传入反向映射函数构建往返操作：
	 *
	 * <pre>{@code
	 * // 编码后通过反向函数解码回来
	 * UnaryCoder<byte[]> roundTrip = base64Coder.toUnary(bytes -> Base64.getDecoder().decode(bytes));
	 * byte[] original = roundTrip.apply(data); // 编码后立即解码
	 * }</pre>
	 *
	 * @param mapper 映射函数（负责 R → T 方向的转换），不可为 null
	 * @return 组合后的同类型转换器
	 */
	default UnaryCoder<T> toUnary(@NonNull Function<? super R, ? extends T> mapper) {
		Coder<T, T> coder = andThen(mapper);
		return coder::apply;
	}
}