package run.soeasy.framework.core.comparator;

import java.util.Objects;

/**
 * 比较的抽象，代表一次比较行为及其结果。
 *
 * <p>
 * 该接口作为比较体系的顶层抽象，仅定义最基本的契约——判断被比较对象是否相等。
 * 具体的比较逻辑由实现类封装，子类或子接口可在此基础上按需扩展（如顺序比较、差异详情等）。
 *
 * <p>
 * 该接口为函数式接口，支持通过 lambda 表达式或静态工厂方法快速创建实例：
 *
 * <pre>{@code
 * Comparison c1 = Comparison.equal();
 * Comparison c2 = Comparison.of(a, b);
 * Comparison c3 = () -> customCheck();
 * }</pre>
 * 
 */
@FunctionalInterface
public interface Comparison {

	/**
	 * 返回一个始终表示"相等"的比较结果。
	 *
	 * <p>
	 * 返回的实例是共享的常量，多次调用返回同一对象。
	 *
	 * @return 始终返回 {@code true} 的比较实例
	 */
	static Comparison equal() {
		return () -> true;
	}

	/**
	 * 返回一个始终表示"不相等"的比较结果。
	 *
	 * <p>
	 * 返回的实例是共享的常量，多次调用返回同一对象。
	 *
	 * @return 始终返回 {@code false} 的比较实例
	 */
	static Comparison notEqual() {
		return () -> false;
	}

	/**
	 * 根据给定的布尔值创建一个比较结果。
	 *
	 * @param isEqual 比较结论
	 * @return 对应的比较实例
	 */
	static Comparison of(boolean isEqual) {
		return () -> isEqual;
	}

	/**
	 * 基于两个对象的 {@link Object#equals} 方法创建比较结果。
	 *
	 * <p>
	 * 对两个参数均为 {@code null} 的情况返回相等结果， 仅其中一个为 {@code null} 时返回不相等结果。
	 *
	 * @param a 第一个对象，可以为 {@code null}
	 * @param b 第二个对象，可以为 {@code null}
	 * @return 基于 {@code equals} 语义的比较实例
	 */
	static Comparison of(Object a, Object b) {
		return () -> Objects.equals(a, b);
	}

	/**
	 * 判断被比较的两个对象是否相等。
	 *
	 * <p>
	 * 实现类应在此方法中封装具体的比较逻辑。 对于延迟求值场景，可在此执行实际的比较计算。
	 *
	 * @return 如果被比较的对象相等则返回 {@code true}，否则返回 {@code false}
	 */
	boolean isEqual();
}