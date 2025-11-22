package run.soeasy.framework.sequences;

import run.soeasy.framework.core.domain.Range;

/**
 * 字符串序列生成器接口。
 * <p>
 * 此接口继承自通用的 {@link Sequence} 接口，专门用于生成字符串类型的序列值。
 * 它在通用序列接口的基础上，增加了一个重要的约定：所有生成的字符串都必须符合
 * 由 {@link #getLengthRange()} 方法指定的长度范围。
 *
 * @author  soeasy.run
 * @see     Sequence
 * @see     Range
 */
public interface StringSequence extends Sequence<String> {

    /**
     * 获取此序列生成器所生成字符串的长度范围。
     * <p>
     * 该方法返回一个 {@link Range} 对象，用于定义所有生成的字符串必须满足的长度约束。
     * 例如，如果返回的范围是 [5, 10]，则生成的每一个字符串的长度都必须大于等于5且小于等于10。
     * <p>
     * 这个约束是强制性的，任何实现此接口的类都必须确保其生成的字符串长度符合该范围。
     * 范围的上下界可以是闭区间（包含）或开区间（不包含），具体由 {@link Range} 对象决定。
     * 如果返回 {@link Range#unbounded()}，则表示对字符串长度没有任何限制（这不太常见）。
     *
     * @return 一个 {@link Range} 对象，指定了生成字符串的长度范围，该范围的上下界均为整数。
     */
    Range<Integer> getLengthRange();
}