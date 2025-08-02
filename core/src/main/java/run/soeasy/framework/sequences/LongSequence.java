package run.soeasy.framework.sequences;

import lombok.NonNull;
import run.soeasy.framework.core.math.LongValue;

/**
 * 长整型序列生成器接口，扩展自{@link NumberSequence}接口，
 * 专门用于生成{@code long}类型的序列值，提供长整型特化方法和默认实现，
 * 适用于需要生成大范围长整型序列的场景（如分布式ID生成、大数据计数）。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>长整型特化：提供{@link #nextLong()}等长整型直接操作方法</li>
 *   <li>类型转换：自动将长整型值封装为{@link LongValue}返回</li>
 *   <li>步长控制：支持自定义步长（正数递增，负数递减）</li>
 *   <li>异常处理：超出有效范围时抛出{@link UnsupportedOperationException}</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>分布式ID生成：生成全局唯一的长整型ID（如雪花算法）</li>
 *   <li>大数据计数：处理亿级以上规模的递增/递减计数</li>
 *   <li>时间戳序列：基于时间戳生成有序长整型序列</li>
 *   <li>分布式事务编号：生成跨系统唯一的事务标识</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see NumberSequence
 * @see LongValue
 */

@FunctionalInterface
public interface LongSequence extends NumberSequence {
    /**
     * 获取下一个序列值（带步长参数）。
     * <p>
     * 1. 将步长转换为长整型
     * 2. 调用{@link #nextLong(long)}获取长整型值
     * 3. 封装为{@link LongValue}并返回
     * 
     * @param step 步长值（不可为null）
     * @return 下一个长整型序列值（自动适配为Number类型）
     * @throws UnsupportedOperationException 当生成值超出有效范围时抛出
     */
    @Override
    @NonNull
    default Number next(Number step) throws UnsupportedOperationException {
        return nextLong(step.longValue());
    }

    /**
     * 获取下一个长整型序列值（使用默认步长）。
     * <p>
     * 等价于调用{@code nextLong(getStep().longValue())}，
     * 默认步长为{@link NumberSequence#DEFAULT_STEP}。
     * 
     * @return 下一个长整型值
     * @throws UnsupportedOperationException 当生成值超出有效范围时抛出
     */
    default long nextLong() throws UnsupportedOperationException {
        return nextLong(getStep().longValue());
    }

    /**
     * 获取下一个长整型序列值（自定义步长）。
     * <p>
     * 实现类应根据步长生成下一个长整型值，
     * 推荐实现以下逻辑：
     * <ul>
     *   <li>正数步长：生成递增序列（如step=2生成2,4,6...）</li>
     *   <li>负数步长：生成递减序列（如step=-1生成5,4,3...）</li>
     * </ul>
     * 
     * @param step 步长值（决定递增/递减幅度）
     * @return 下一个长整型值
     * @throws UnsupportedOperationException 当无法生成有效序列值时抛出
     */
    long nextLong(long step) throws UnsupportedOperationException;
    
    @Override
    default NumberSequence step(@NonNull Number step) {
    	return new SpecifiedStepsLongSequence<>(this, step);
    }
}