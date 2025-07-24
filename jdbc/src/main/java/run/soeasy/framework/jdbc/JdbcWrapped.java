package run.soeasy.framework.jdbc;

import java.sql.SQLException;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapped;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.PipelineWrapper;

/**
 * JDBC操作的管道包装器，继承自{@link Wrapped}并实现{@link PipelineWrapper}接口，
 * 用于包装处理JDBC结果的{@link Pipeline}对象，统一管理JDBC操作中的管道处理流程与异常（{@link SQLException}），
 * 提供JDBC场景下管道操作的标准化包装实现。
 * 
 * <p>该类通过包装{@link Pipeline<T, SQLException>}，将JDBC相关的结果处理、异常传递与管道模式结合，
 * 便于构建链式的JDBC操作流程（如查询结果处理、数据转换、异常捕获等）。
 * 
 * @param <T> 管道处理的结果类型
 * @author soeasy.run
 * @see Wrapped
 * @see PipelineWrapper
 * @see Pipeline
 * @see SQLException
 */
public class JdbcWrapped<T> extends Wrapped<Pipeline<T, SQLException>>
		implements PipelineWrapper<T, SQLException, Pipeline<T, SQLException>> {

    /**
     * 构造JDBC管道包装器
     * 
     * @param source 待包装的JDBC管道（处理T类型结果，可能抛出SQLException，非空）
     */
	public JdbcWrapped(@NonNull Pipeline<T, SQLException> source) {
		super(source);
	}
}