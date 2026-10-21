package run.soeasy.framework.commons.domain;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 表格分页通用响应结果类
 * <p>
 * 继承自 {@link Result}，封装分页查询场景必需的核心字段，统一表格分页接口的响应格式，
 * 适配前端表格组件（如 Element UI Table、Ant Design Table）的分页数据展示需求。
 *
 * @author soeasy.run
 * @param <T> 表格行数据泛型类型（单条行记录的数据类型，如实体DTO、VO）
 * @see Result 无数据载体的核心响应基类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class TableResult<T> extends Result {

    /**
     * 序列化版本号（确保对象序列化/反序列化兼容性）
     */
    private static final long serialVersionUID = 1L;

    /**
     * 符合查询条件的总数据条数
     * <p>
     * 用于前端计算总页数和分页导航展示（如"共 N 条数据"），对应数据库查询的总记录数
     */
    private long total;

    /**
     * 当前页的行数据列表
     * <p>
     * 存储分页查询后当前页的具体数据集合，每条数据为泛型指定的单条记录类型
     */
    private List<T> rows;

    /**
     * 基于核心响应实例构建分页响应
     * <p>
     * 复用父类 {@link Result} 的状态信息（code、msg、success），初始化分页响应对象
     *
     * @param result 核心响应实例（包含状态码、提示消息、成功标识，可为 null）
     */
    public TableResult(Result result) {
        super(result);
    }
}