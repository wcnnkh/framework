package run.soeasy.framework.sequences;

import java.util.UUID;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Range;

/**
 * UUID序列生成器接口。
 * <p>
 * 此接口继承自 {@link StringSequence}，专门用于生成 UUID 字符串。
 * 它提供了默认实现，确保生成的字符串是移除了连字符('-')的标准UUID格式， 因此长度固定为32位。
 * <p>
 * 作为一个 {@link FunctionalInterface}，它的核心是 {@link #nextUUID()} 方法，
 * 允许使用者以lambda表达式的形式提供自定义的UUID生成逻辑。
 *
 * @author soeasy.run
 * @see StringSequence
 * @see UUID
 * @see #nextUUID()
 */
@FunctionalInterface
public interface UUIDSequence extends StringSequence {
	public static final UUIDSequence RANDOM = () -> UUID.randomUUID();

	/**
	 * 获取此序列生成器生成的字符串长度范围。
	 * <p>
	 * UUID的标准字符串表示（不含连字符）长度固定为32个字符。 因此，此方法返回一个固定的范围 {@code Range.just(32)}。
	 *
	 * @return 一个包含单个值32的范围，表示所有生成的字符串长度都为32
	 */
	@Override
	default Range<Integer> getLengthRange() {
		return Range.just(32);
	}

	/**
	 * 生成并返回下一个 UUID 字符串。
	 * <p>
	 * 此方法为 {@link StringSequence} 接口提供了默认实现。 它通过调用 {@link #nextUUID()} 获取一个
	 * {@link UUID} 对象， 然后将其转换为字符串并移除所有连字符('-')。
	 *
	 * @return 下一个 UUID 字符串，长度为32，不可为null
	 */
	@Override
	default @NonNull String next() {
		return nextUUID().toString().replace("-", "");
	}

	/**
	 * 生成并返回下一个 {@link UUID} 对象。
	 * <p>
	 * 这是此函数式接口的核心方法。实现类或lambda表达式需提供具体的UUID生成策略。
	 *
	 * @return 下一个 {@link UUID} 对象，不可为null
	 */
	@NonNull
	UUID nextUUID();
}