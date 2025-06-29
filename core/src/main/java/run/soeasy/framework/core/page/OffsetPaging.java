package run.soeasy.framework.core.page;

import java.util.List;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Listable;

/**
 * 使用偏移量进行分页
 * 基于偏移量(offset)和每页大小(pageSize)实现的分页机制
 * 
 * @author soeasy.run
 *
 * @param <V> 分页内容的元素类型
 */
public class OffsetPaging<V> extends CursorPaging<Long, V> {
    /**
     * 在内存中对list进行分页
     * 
     * @param offset 偏移量，从0开始
     * @param pageSize 每页大小
     * @param elements 原始数据列表
     */
    public OffsetPaging(long offset, int pageSize, List<V> elements) {
        this(elements.size(), offset, pageSize, (cursorId, length) -> {
            int fromIndex = Math.toIntExact(cursorId);
            if (fromIndex >= elements.size()) {
                return Listable.empty();
            }
            List<V> list = elements.subList(fromIndex, Math.min(fromIndex + length, elements.size()));
            return Listable.forCollection(list);
        });
    }

    /**
     * 未知数量的构造
     * 
     * @param offset 偏移量，从0开始
     * @param pageSize 每页大小
     * @param pagingQuery 分页查询器，不可为null
     */
    public OffsetPaging(long offset, int pageSize, @NonNull PagingQuery<Long, Listable<V>> pagingQuery) {
        super(offset, pageSize, (cursorId, length) -> {
            Listable<V> listable = pagingQuery.query(cursorId, length);
            return new Cursor<>(cursorId, listable, listable.hasElements() ? (cursorId + length) : null);
        });
    }

    /**
     * 已知总数的构造
     * 
     * @param total 总记录数
     * @param offset 偏移量，从0开始
     * @param pageSize 每页大小
     * @param pagingQuery 分页查询器，不可为null
     */
    public OffsetPaging(long total, long offset, int pageSize, @NonNull PagingQuery<Long, Listable<V>> pagingQuery) {
        super(total, offset, pageSize, (cursorId, length) -> {
            long nextCursorId = cursorId + length;
            return new Cursor<>(cursorId, pagingQuery.query(cursorId, length),
                    nextCursorId < total ? nextCursorId : null);
        });
    }

    /**
     * 获取当前页码
     * 
     * @return 当前页码，从1开始
     */
    public final long getPageNumber() {
        return (getCursorId() / getPageSize()) + 1;
    }

    /**
     * 跳转到指定页码
     * 
     * @param pageNumber 目标页码，从1开始
     * @return 新的OffsetPaging实例
     */
    public final OffsetPaging<V> jumpToPage(long pageNumber) {
        return jumpToPage(pageNumber, getPageSize());
    }

    /**
     * 跳转到指定页码并指定每页大小
     * 
     * @param pageNumber 目标页码，从1开始
     * @param pageSize 每页大小
     * @return 新的OffsetPaging实例
     */
    public OffsetPaging<V> jumpToPage(long pageNumber, int pageSize) {
        return new OffsetPaging<>(getTotal(), pageNumber * pageSize, pageSize, this::query);
    }
}