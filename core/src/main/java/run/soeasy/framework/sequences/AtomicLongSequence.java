package run.soeasy.framework.sequences;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 基于原子操作的长整型序列生成器，继承自{@link AtomicLong}并实现{@link LongSequence}接口，
 * 提供线程安全的长整型序列生成功能，适用于高并发环境下的唯一ID生成或计数器场景。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>原子操作：基于{@link AtomicLong}实现，确保多线程环境下的线程安全</li>
 *   <li>步长控制：支持自定义步长（通过构造参数设置），默认步长为1</li>
 *   <li>高性能：无锁设计，适合高并发场景下的高频序列生成</li>
 *   <li>连续递增：每次调用生成的序列值按步长严格递增</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>分布式ID生成：生成全局唯一的长整型ID（需配合分布式协调机制）</li>
 *   <li>高并发计数器：多线程环境下的递增计数（如请求量统计）</li>
 *   <li>序列号生成：生成连续的订单号、流水号等业务序列号</li>
 *   <li>本地缓存索引：生成缓存数据的索引标识</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see AtomicLong
 * @see LongSequence
 */
public class AtomicLongSequence extends AtomicLong implements LongSequence {
    private static final long serialVersionUID = 1L;

    /**
     * 创建指定初始值的原子长整型序列生成器。
     * <p>
     * 步长默认为{@link LongSequence#DEFAULT_STEP}（即1），
     * 生成的序列值范围遵循{@link LongSequence}的默认范围。
     * 
     * @param initialValue 序列初始值
     */
    public AtomicLongSequence(long initialValue) {
        super(initialValue);
    }

    /**
     * 生成下一个长整型序列值（按步长递增）。
     * <p>
     * 基于原子操作{@link #addAndGet(long)}实现，确保线程安全：
     * <ul>
     *   <li>正数步长：生成递增序列（如step=2生成2,4,6...）</li>
     *   <li>负数步长：生成递减序列（如step=-1生成5,4,3...）</li>
     * </ul>
     * <p>
     * <b>注意：</b>范围检查由上层接口{@link LongSequence}保证，
     * 超出范围时由调用方处理异常。
     * 
     * @param step 步长值（决定递增/递减幅度）
     * @return 增加后的长整型值
     * @throws UnsupportedOperationException 当生成值超出接口定义的范围时抛出
     */
    @Override
    public long nextLong(long step) throws UnsupportedOperationException {
        return addAndGet(step);
    }
}