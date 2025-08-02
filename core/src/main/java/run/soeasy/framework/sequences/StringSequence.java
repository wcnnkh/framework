package run.soeasy.framework.sequences;

import lombok.NonNull;

/**
 * 字符串序列生成器接口，扩展自{@link Sequence}并标记为函数式接口，
 * 提供字符串序列生成功能，支持动态指定长度和链式调用，适用于ID生成、序列号生成等场景。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>函数式设计：可作为lambda表达式或方法引用的目标类型</li>
 *   <li>长度控制：通过{@link #next(int)}指定生成字符串长度，{@link #getLength()}获取默认长度</li>
 *   <li>链式调用：通过{@link #length(int)}创建固定长度的新序列生成器</li>
 *   <li>默认实现：{@link #next()}方法使用默认长度生成字符串</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>随机ID生成：生成指定长度的随机字符串（如"a3x5"）</li>
 *   <li>有序序列号：生成递增的字符串序列（如"ORD001", "ORD002"）</li>
 *   <li>安全令牌：生成固定长度的加密令牌</li>
 *   <li>业务编号：生成包含业务规则的字符串编号（如日期+流水号）</li>
 * </ul>
 * 
 * @author soeasy.run
 */
@FunctionalInterface
public interface StringSequence extends Sequence<String> {
    /**
     * 获取默认字符串长度（默认返回0表示未知长度）。
     * <p>
     * 实现类可覆盖此方法返回默认长度：
     * <ul>
     *   <li>返回0：表示长度由{@link #next()}的调用方决定</li>
     *   <li>返回正数：表示{@link #next()}使用该长度生成字符串</li>
     * </ul>
     * 
     * @return 字符串长度，0表示未知
     */
    default int getLength() {
        return 0;
    }

    /**
     * 创建固定长度的字符串序列生成器（装饰器模式）。
     * <p>
     * 返回一个新的{@link StringSequence}实例，其{@link #next()}方法
     * 将始终使用指定长度生成字符串，等价于调用{@code next(length)}。
     * 
     * @param length 目标字符串长度（≥0）
     * @return 新的固定长度序列生成器，不可为null
     */
    default StringSequence length(int length) {
        return new SpecifiedLengthStringSequence<>(this, length);
    }

    /**
     * 获取下一个字符串序列值（默认实现）。
     * <p>
     * 调用{@link #next(int)}并使用{@link #getLength()}的返回值，
     * 若{@link #getLength()}=0则由实现类决定长度。
     * 
     * @return 下一个字符串值，不可为null
     * @throws UnsupportedOperationException 当无法生成有效字符串时抛出
     */
    @Override
    default @NonNull String next() throws UnsupportedOperationException {
        return next(getLength());
    }

    /**
     * 获取指定长度的字符串序列值（核心方法）。
     * <p>
     * 实现类应根据长度生成符合规则的字符串，常见实现包括：
     * <ul>
     *   <li>数字序列："0001"（length=4）</li>
     *   <li>字母数字混合："aB3c"（length=4）</li>
     *   <li>时间戳序列："20231101"（length=8）</li>
     * </ul>
     * 
     * @param length 目标字符串长度（≥0）
     * @return 生成的字符串值，长度必须等于参数length，不可为null
     * @throws UnsupportedOperationException 当length&lt;0或无法生成有效字符串时抛出
     */
    String next(int length) throws UnsupportedOperationException;
}