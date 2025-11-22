package run.soeasy.framework.sequences;

/**
 * 长整数计数器接口，继承自通用计数器接口，专门用于处理Long类型的计数操作。
 * 提供了基于长整数类型的快照功能，用于获取指定大小的连续长整数序列。
 */
public interface LongCounter extends Counter<Long> {
	/**
	 * 获取当前计数器的快照，返回一个包含指定指定大小的连续长整数的序列。
	 * 该方法会从当前计数器获取指定大小的连续长整数范围，范围为[min, max)，其中max = min + size。
	 * 
	 * @param size 所需的序列大小，为非负长整数
	 * @return 包含连续长整数序列的Sequence对象，序列中的长整数从min到max-1
	 */
	@Override
	default Sequence<Long> snapshot(long size) {
		Long max = next(size);
		Long min = max - size;
		return new AtomicLongCounter(min, min, max);
	}
}