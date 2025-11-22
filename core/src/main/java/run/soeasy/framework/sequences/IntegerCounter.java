package run.soeasy.framework.sequences;

/**
 * 整数计数器接口，继承自通用计数器接口，专门用于处理Integer类型的计数操作。
 * 提供了基于整数类型的快照功能，用于获取指定大小的连续整数序列。
 */
public interface IntegerCounter extends Counter<Integer> {
	/**
	 * 获取当前计数器的快照，返回一个包含指定数量连续整数的序列。
	 * 该方法会从当前计数器获取指定大小的连续整数范围，范围为[min, max)，其中max = min + size。
	 * 
	 * @param size 所需的序列大小，必须为非负整数且不超过Integer的取值范围
	 * @return 包含连续整数序列的Sequence对象，序列中的整数从min到max-1
	 */
	@Override
	default Sequence<Integer> snapshot(long size) {
		int availableSize = Math.toIntExact(size);
		int max = next(availableSize);
		int min = max - availableSize;
		return new AtomicIntegerCounter(min, min, max);
	}
}