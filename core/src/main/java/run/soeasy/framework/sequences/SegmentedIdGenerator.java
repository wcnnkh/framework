package run.soeasy.framework.sequences;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 分段式长整数ID生成器
 * <p>
 * 基于基础长整数序列生成器，通过预取一段ID范围进行本地分配，减少对基础序列的访问频率，
 * 适用于高并发场景下提升ID生成性能。
 * </p>
 */
@Getter
@RequiredArgsConstructor
public class SegmentedIdGenerator implements Sequence<Long> {

    /** 基础长整数序列生成器（提供分段ID的起始值） */
    @NonNull
    private final LongSequence longSequence;

    /** 每段ID的数量上限（必须为正整数） */
    private final int segmentSize;

    /** 
     * 当前分段的起始ID
     * 加volatile保证多线程间的内存可见性
     */
    private volatile long currentSegmentStart = -1;

    /** 
     * 当前分段内的偏移量（已分配的ID数量）
     * 使用AtomicLong保证原子性和可见性，避免同步块
     */
    private final AtomicLong offsetInSegment = new AtomicLong(0);

    /**
     * 获取下一个ID
     * <p>
     * 当当前分段的ID分配完毕后，会自动从基础序列获取下一个分段。
     * 线程安全，支持高并发场景下的高效ID生成。
     * </p>
     *
     * @return 下一个唯一的长整数ID
     * @throws UnsupportedOperationException 当基础序列生成器无法提供有效ID时抛出
     */
    @Override
    public @NonNull Long next() throws UnsupportedOperationException {
        // 双重检查锁定：仅在分段耗尽时进入同步块
        if (isCurrentSegmentExhausted()) {
            synchronized (this) {
                if (isCurrentSegmentExhausted()) {
                    refreshSegment();
                }
            }
        }

        // 使用AtomicLong的原子操作获取并递增偏移量，无需同步块
        long currentOffset = offsetInSegment.getAndIncrement();
        return currentSegmentStart + currentOffset;
    }

    /**
     * 检查当前分段是否已耗尽
     *
     * @return 若当前分段未初始化或已分配完所有ID，则返回true
     */
    private boolean isCurrentSegmentExhausted() {
        // 注意：offsetInSegment.get()是原子操作，无需额外同步
        return currentSegmentStart < 0 || offsetInSegment.get() >= segmentSize;
    }

    /**
     * 刷新分段：从基础序列获取新的分段起始值
     * <p>
     * 新分段的起始值 = 基础序列返回值 - 分段大小，确保基础序列返回值是当前分段的最后一个ID
     * </p>
     */
    private void refreshSegment() {
        // 从基础序列获取下一个分段的结束值
        long segmentEnd = longSequence.nextLong(segmentSize);
        
        // 验证基础序列返回值的有效性
        validateSegmentEnd(segmentEnd);
        
        // 计算新分段的起始值并重置偏移量
        this.currentSegmentStart = segmentEnd - segmentSize;
        this.offsetInSegment.set(0);
    }

    /**
     * 验证分段结束值的有效性
     *
     * @param segmentEnd 基础序列返回的分段结束值
     * @throws IllegalArgumentException 当结束值小于分段大小或出现分段回退时抛出
     */
    private void validateSegmentEnd(long segmentEnd) {
        if (segmentEnd < segmentSize) {
            throw new IllegalArgumentException(
                String.format("Invalid segment end value: %d, must be greater than or equal to segment size: %d",
                    segmentEnd, segmentSize)
            );
        }
        
        // 校验新分段的起始值是否大于上一段的结束值（保证递增性）
        if (currentSegmentStart >= 0) { // 排除首次初始化场景
            long previousSegmentEnd = currentSegmentStart + segmentSize - 1;
            long newSegmentStart = segmentEnd - segmentSize;
            if (newSegmentStart <= previousSegmentEnd) {
                throw new IllegalArgumentException(
                    String.format("Segment backtrack detected: new segment starts at %d, which is not after previous end %d",
                        newSegmentStart, previousSegmentEnd)
                );
            }
        }
    }

    /**
     * 获取当前分段信息，用于调试和监控
     *
     * @return 包含当前分段起始值、分段大小和剩余ID数量的字符串
     */
    public String getCurrentSegmentInfo() {
        long currentOffset = offsetInSegment.get();
        long remaining = isCurrentSegmentExhausted() ? 0 : segmentSize - currentOffset;
        return String.format("Segment [start=%d, size=%d, remaining=%d]",
            currentSegmentStart, segmentSize, remaining);
    }
    
    @Override
    public String toString() {
    	return getCurrentSegmentInfo();
    }

    /**
     * 重写equals方法，确保相同配置的生成器实例被视为相等
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SegmentedIdGenerator that = (SegmentedIdGenerator) o;
        return segmentSize == that.segmentSize &&
            Objects.equals(longSequence, that.longSequence);
    }

    /**
     * 重写hashCode方法，与equals保持一致
     */
    @Override
    public int hashCode() {
        return Objects.hash(longSequence, segmentSize);
    }
}
