package run.soeasy.framework.commons.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 带业务数据的通用响应结果类
 * <p>
 * 继承自 {@link Result}，在核心状态信息（code、msg、success）基础上扩展业务数据载体，
 * 适用于需要返回具体数据的接口场景（如列表查询、详情查询、数据提交后返回结果），
 * 保持接口响应格式的统一性和完整性。
 *
 * @author soeasy.run
 * @param <T> 业务数据泛型类型（支持任意数据类型，如实体类、集合、基本类型等）
 * @see Result 无数据载体的核心响应基类
 */
@Data
@EqualsAndHashCode(callSuper = true) // 生成 equals/hashCode 时包含父类字段
@ToString(callSuper = true) // 生成 toString 时包含父类字段（便于日志打印完整响应信息）
@NoArgsConstructor
public class DataResult<T> extends Result {

    /**
     * 序列化版本号（确保对象序列化/反序列化时的兼容性，与父类保持版本独立）
     */
    private static final long serialVersionUID = 1L;

    /**
     * 业务数据载体
     * <p>
     * 存储接口返回的具体业务数据，如：
     * - 单个实体对象（如 UserDTO、OrderDTO）
     * - 集合数据
     * - 基本类型数据（如 Integer、String、Boolean）
     * - 空值（无数据时为 null，不影响响应格式完整性）
     */
    private T data;

    /**
     * 基于核心响应实例构建带数据载体的响应
     * <p>
     * 复用父类 {@link Result} 的状态信息（code、msg、success），初始化空数据载体，
     * 适用于从已有核心响应扩展数据的场景（如通过 {@link Result#data(Object)} 方法调用）。
     *
     * @param result 核心响应实例（包含 code、msg、success 状态信息，可为 null）
     */
    public DataResult(Result result) {
        super(result);
    }

    /**
     * 拷贝构造方法（基于已有 DataResult 实例构建新实例）
     * <p>
     * 同时复用父类状态信息（code、msg、success）和源实例的业务数据（data），
     * 适用于需要复制响应结果并修改部分字段的场景（如响应转发、数据二次处理）。
     *
     * @param dataResult 源 DataResult 实例（可为 null，null 时状态信息和数据均为默认值）
     * @param <S> 源数据泛型类型（支持子类泛型向父类泛型兼容）
     */
    public <S extends T> DataResult(DataResult<S> dataResult) {
        super(dataResult);
        if (dataResult != null) {
            this.data = dataResult.data;
        }
    }
}