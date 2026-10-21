package run.soeasy.framework.codec;

import java.util.function.UnaryOperator;

import run.soeasy.framework.core.comparator.Evaluator;

/**
 * 同类型单向数据转换器接口，继承 {@link Coder} 和 {@link UnaryOperator}，
 * 提供输入输出同类型的数据转换、校验以及函数迭代（重复执行）能力。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li><b>同类型转换</b>：输入输出均为同一类型 {@code T}，适用于数据加工、 归一化、加密/解密（同一算法）、压缩/解压等场景</li>
 * <li><b>JDK 互操作</b>：继承 {@link UnaryOperator}，可无缝替换所有 接受 {@code UnaryOperator}
 * 的 API（如 {@link java.util.stream.Stream#map}、 {@link java.util.Optional#map}
 * 等）</li>
 * <li><b>结果校验</b>：继承 {@link Coder} 的 {@link Coder#check(Object, Object)}
 * 能力，可对转换结果进行断言和验证</li>
 * <li><b>函数迭代</b>：通过 {@link #repeated(int)} 将当前转换连续执行多次， 形成 fⁿ(x) 的效果</li>
 * </ul>
 *
 * <p>
 * <b>与 {@link Coder} 的关系：</b>
 * <ul>
 * <li>{@code Coder<T, T>} 是输入输出同类型的特化形式</li>
 * <li>{@code UnaryCoder<T>} 为其提供语义化命名和额外能力（{@link #repeated(int)}）</li>
 * <li>所有 {@code Coder} 的组合、校验能力在 {@code UnaryCoder} 中完全可用</li>
 * </ul>
 *
 * <p>
 * <b>与 {@link UnaryOperator} 的关系：</b>
 * <ul>
 * <li>{@code UnaryOperator<T>} 是 JDK 标准库中 {@code Function<T, T>} 的特化</li>
 * <li>{@code UnaryCoder<T>} 在 {@code UnaryOperator} 基础上增加了 {@link Evaluator}
 * 校验能力和 {@link #repeated(int)} 迭代能力</li>
 * <li>任何接受 {@code UnaryOperator<T>} 的地方都可以传入 {@code UnaryCoder<T>}</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>数据加工：字符串修剪、大小写转换、数值运算等</li>
 * <li>加密/解密：同一算法既可用于加密也可用于解密（如 XOR、ROT13）</li>
 * <li>压缩/解压：同一算法支持双向操作</li>
 * <li>函数迭代：多次应用同一转换（如哈希多次加盐、循环位移）</li>
 * <li>数据校验：转换后与期望值比对</li>
 * </ul>
 *
 * <p>
 * <b>使用示例：</b>
 * 
 * <pre>{@code
 * // 创建 UnaryCoder（Lambda 方式）
 * UnaryCoder<String> trim = String::trim;
 * UnaryCoder<Integer> addOne = i -> i + 1;
 *
 * // 直接转换
 * String result = trim.apply("  hello  "); // "hello"
 *
 * // 结果校验
 * Comparison comparison = trim.check("  test  ", "test");
 * if (comparison.isMatch()) { ... }
 *
 * // 函数迭代：连续执行 3 次
 * UnaryCoder<Integer> addThree = addOne.repeated(3);
 * Integer output = addThree.apply(0); // 3
 *
 * // 当 UnaryOperator 使用
 * stream.map(trim).collect(Collectors.toList());
 *
 * // 恒等转换器
 * UnaryCoder<Object> identity = Coder.identity();
 * Object same = identity.apply(obj); // 返回 obj 本身
 * }</pre>
 *
 * @author soeasy.run
 * @param <T> 数据类型（输入输出均为该类型）
 * @see Coder
 * @see UnaryOperator
 * @see Codec
 */
@FunctionalInterface
public interface UnaryCoder<T> extends Coder<T, T>, UnaryOperator<T> {

	/**
	 * 返回一个新 {@link UnaryCoder}，将当前转换连续执行指定次数。
	 * <p>
	 * 数学上等价于函数迭代 fⁿ(x)：
	 * 
	 * <pre>
	 * repeated(0) → x（恒等，直接返回输入）
	 * repeated(1) → f(x)
	 * repeated(3) → f(f(f(x)))
	 * </pre>
	 *
	 * <p>
	 * 特殊情况优化：
	 * <ul>
	 * <li>{@code count == 0}：返回 {@link Coder#identity()}，不执行任何转换</li>
	 * <li>{@code count == 1}：返回当前实例本身，零额外开销</li>
	 * <li>{@code count > 1}：返回组合后的新转换器</li>
	 * </ul>
	 *
	 * @param count 执行次数，必须 ≥ 0
	 * @return 重复执行后的新 {@link UnaryCoder}
	 * @throws IllegalArgumentException 如果 count < 0
	 */
	default UnaryCoder<T> repeated(int count) {
		if (count < 0) {
			throw new IllegalArgumentException("count must be non-negative");
		}

		if (count == 0) {
			return Coder.identity();
		}

		if (count == 1) {
			return this;
		}

		return (source) -> {
			T target = source;
			for (int i = 0; i < count; i++) {
				target = apply(target);
			}
			return target;
		};
	}
}