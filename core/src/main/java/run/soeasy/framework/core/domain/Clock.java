package run.soeasy.framework.core.domain;

/**
 * 时间源抽象接口。
 * <p>
 * 该接口提供了一种获取当前时间戳的统一方式，其核心目的是将时间获取逻辑与具体实现解耦，
 * 从而提高代码的可测试性和灵活性。通过依赖此抽象而非具体实现，你可以在测试中轻松模拟不同的时间。
 * <p>
 * 此接口被设计为函数式接口，这意味着它可以被用作 Lambda 表达式或方法引用。
 *
 * @author soeasy.run
 */
@FunctionalInterface
public interface Clock {

	/**
	 * 一个默认的、使用系统当前时间的 {@code Clock} 实现。
	 * <p>
	 * 它的行为等同于调用 {@link System#currentTimeMillis()}。 这是一个便捷的常量，适用于大多数生产环境场景。
	 */
	Clock SYSTEM = System::currentTimeMillis;

	/**
	 * 获取当前时间戳，单位为毫秒。
	 * <p>
	 * 时间戳定义为从 Unix 纪元（1970 年 1 月 1 日 00:00:00 UTC）到当前时间的毫秒数。
	 *
	 * @return 当前时间戳（毫秒级）
	 */
	long millis();
}