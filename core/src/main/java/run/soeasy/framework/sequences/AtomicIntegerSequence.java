package run.soeasy.framework.sequences;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于原子操作的整型序列生成器，继承自{@link AtomicInteger}并实现{@link IntSequence}接口，
 * 提供线程安全的整型序列生成功能，适用于高并发环境下的计数器或有限范围ID生成场景。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>原子操作：基于{@link AtomicInteger}实现，确保多线程环境下的线程安全</li>
 *   <li>步长控制：支持自定义步长（通过方法参数设置），默认步长为1</li>
 *   <li>高性能：无锁设计，适合高并发场景下的高频序列生成</li>
 *   <li>连续递增：每次调用生成的序列值按步长严格递增</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>本地计数器：多线程环境下的递增计数（如请求量统计、任务计数）</li>
 *   <li>有限ID生成：在指定范围内生成唯一整型ID（如1-1000的循环ID）</li>
 *   <li>循环序列：结合步长生成循环使用的整型序列（如0,1,2,0,1,2...）</li>
 *   <li>分页索引：生成分页查询的页码索引序列</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see AtomicInteger
 * @see IntSequence
 */
public class AtomicIntegerSequence extends AtomicInteger implements IntSequence {
    private static final long serialVersionUID = 1L;

    /**
     * 创建指定初始值的原子整型序列生成器。
     * <p>
     * 步长默认为{@link IntSequence#DEFAULT_STEP}（即1），
     * 生成的序列值范围遵循{@link IntSequence}的默认范围。
     * 
     * @param initialValue 序列初始值
     */
    public AtomicIntegerSequence(int initialValue) {
        super(initialValue);
    }

    /**
     * 生成下一个整型序列值（按步长递增）。
     * <p>
     * 基于原子操作{@link #addAndGet(int)}实现，确保线程安全：
     * <ul>
     *   <li>正数步长：生成递增序列（如step=2生成2,4,6...）</li>
     *   <li>负数步长：生成递减序列（如step=-1生成5,4,3...）</li>
     * </ul>
     * <p>
     * <b>注意：</b>范围检查由上层接口{@link IntSequence}保证，
     * 超出范围时由调用方处理异常。
     * 
     * @param step 步长值（决定递增/递减幅度）
     * @return 增加后的整型值
     * @throws UnsupportedOperationException 当生成值超出接口定义的范围时抛出
     */
    @Override
    public int nextInt(int step) throws UnsupportedOperationException {
        return addAndGet(step);
    }
}