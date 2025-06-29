package run.soeasy.framework.core.page;

import java.util.NoSuchElementException;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.LinkedIterator;

/**
 * 分页接口
 * 定义基于游标的分页查询机制，支持获取分页元数据和遍历所有页
 * 
 * @author soeasy.run
 *
 * @param <K> 游标的类型，用于标识分页位置
 * @param <V> 分页内容的元素类型
 */
public interface Paging<K, V> extends Pageable<K, V>, PagingQuery<K, Paging<K, V>> {
    /**
     * 判断是否已知总数
     * 某些场景下（如大数据集）可能无法高效获取总数，此时返回false
     * 未知总数的分页在调用getTotal()时会使用代价较高的循环计数
     * 
     * @return 如果已知总数返回true，否则返回false
     */
    boolean isKnowTotal();

    /**
     * 获取总记录数
     * 如果isKnowTotal()返回false，此方法可能会触发全量数据遍历，性能开销较大
     * 
     * @return 总记录数
     */
    long getTotal();

    /**
     * 获取总页数
     * 计算公式为：ceil(总记录数 / 每页数量)
     * 
     * @return 总页数
     * @throws ArithmeticException 如果pageSize为0
     */
    default long getPages() {
        return (long) Math.ceil((double) getTotal() / getPageSize());
    }

    /**
     * 获取每页数量
     * 
     * @return 每页数量，如果返回0表示每页数量不确定
     */
    int getPageSize();

    /**
     * 获取下一页
     * 
     * @return 下一页的分页对象
     * @throws NoSuchElementException 如果没有下一页
     */
    default Paging<K, V> nextPage() {
        if (!hasNextPage()) {
            throw new NoSuchElementException("There is no next page");
        }
        return jumpTo(getNextCursorId());
    }

    /**
     * 获取所有页的元素集合
     * 支持流式遍历所有页，延迟加载后续页的数据
     * 
     * @return 包含所有页的元素集合
     */
    default Elements<Paging<K, V>> pages() {
        return Elements.of(() -> new LinkedIterator<>(this, Paging::hasNextPage, Paging::nextPage));
    }

    /**
     * 跳转到指定游标位置的页
     * 
     * @param cursorId 目标页的游标ID
     * @return 指定位置的分页对象
     */
    default Paging<K, V> jumpTo(K cursorId) {
        return query(cursorId, getPageSize());
    }
}