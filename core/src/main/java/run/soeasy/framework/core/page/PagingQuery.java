package run.soeasy.framework.core.page;

/**
 * 分页查询接口
 * 定义基于游标的分页查询机制，支持指定游标位置和每页大小进行数据查询
 * 
 * @author soeasy.run
 *
 * @param <S> 游标的类型，用于标识分页位置
 * @param <T> 查询结果的类型，通常是实现了Paging接口的分页对象
 */
public interface PagingQuery<S, T> {
    /**
     * 根据指定游标位置和每页大小进行分页查询
     * 
     * @param cursorId 起始游标位置，用于标识从何处开始查询数据
     *                 首次查询时可为null，表示从第一条数据开始
     * @param pageSize 每页返回的记录数量上限
     *                 0表示使用默认的每页大小（由具体实现决定）
     * @return 返回一个包含查询结果的分页对象
     */
    T query(S cursorId, int pageSize);
}