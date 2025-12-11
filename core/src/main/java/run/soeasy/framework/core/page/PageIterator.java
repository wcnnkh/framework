package run.soeasy.framework.core.page;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.streaming.Streamable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 基于元素迭代器的分页迭代器
 * 将单元素迭代器拆分为分页对象迭代器，线程安全，支持预加载。
 *
 * @param <E> 分页元素类型
 */
public class PageIterator<E> implements Iterator<Page<Long, E>> {

    /**
     * 每页元素数量（>0）
     */
    @Getter
    private final int pageSize;

    /**
     * 原始元素迭代器
     */
    @NonNull
    private final Iterator<? extends E> sourceIterator;

    /**
     * 待获取的页码（从1开始）
     */
    private int pendingPageNumber = 1;

    /**
     * 预加载的下一页数据
     */
    private Page<Long, E> cachedPage;

    /**
     * 创建分页迭代器
     *
     * @param pageSize        每页大小，必须&gt;0
     * @param sourceIterator  原始元素迭代器，非空
     * @throws IllegalArgumentException pageSize≤0时抛出
     * @throws NullPointerException     sourceIterator为null时抛出
     */
    public PageIterator(int pageSize, @NonNull Iterator<? extends E> sourceIterator) {
        Assert.isTrue(pageSize > 0, () -> String.format("Page size must be greater than 0, current value: %d", pageSize));
        this.pageSize = pageSize;
        this.sourceIterator = sourceIterator;
    }

    /**
     * 判断是否存在下一页数据
     *
     * @return true=存在下一页，false=无更多数据
     */
    @Override
    public synchronized boolean hasNext() {
        if (cachedPage != null) {
            return true;
        }
        if (!sourceIterator.hasNext()) {
            return false;
        }

        List<E> elements = collectPageElements();
        long currentOffset = OffsetPaging.getOffset(pendingPageNumber, pageSize);
        Long nextCursorId = calculateNextCursorId(currentOffset);

        Slice<Long, E> slice = new CursorSlice<>(currentOffset, Streamable.of(elements), nextCursorId, null);
        cachedPage = new Page<>(slice, pendingPageNumber, pageSize);
        pendingPageNumber++;
        return true;
    }

    /**
     * 获取下一页分页对象
     *
     * @return 下一页分页对象
     * @throws NoSuchElementException 无更多数据时抛出
     */
    @Override
    public synchronized Page<Long, E> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more pagination data available, current traversed to page " + pendingPageNumber);
        }

        Page<Long, E> currentPage = cachedPage;
        cachedPage = null;
        return currentPage;
    }

    /**
     * 收集当前页元素（最多pageSize个）
     *
     * @return 当前页元素列表
     */
    private List<E> collectPageElements() {
        List<E> elements = new ArrayList<>(pageSize);
        while (sourceIterator.hasNext() && elements.size() < pageSize) {
            elements.add(sourceIterator.next());
        }
        return elements;
    }

    /**
     * 计算下一页游标ID（防止数值溢出）
     *
     * @param currentOffset 当前页偏移量
     * @return 下一页游标ID
     * @throws ArithmeticException 数值溢出时抛出
     */
    private Long calculateNextCursorId(long currentOffset) {
        try {
            return Math.addExact(currentOffset, pageSize);
        } catch (ArithmeticException e) {
            throw new ArithmeticException(
                    String.format("Failed to calculate next cursor ID: numeric overflow (current offset=%d, page size=%d)",
                            currentOffset, pageSize)
            );
        }
    }

    /**
     * 不支持移除操作
     *
     * @throws UnsupportedOperationException 调用时抛出
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Page iterator does not support remove operation");
    }
}