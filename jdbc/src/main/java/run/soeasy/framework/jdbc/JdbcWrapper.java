package run.soeasy.framework.jdbc;

import java.sql.SQLException;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapped;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.PipelineWrapper;

public class JdbcWrapper<T> extends Wrapped<Pipeline<T, SQLException>>
		implements PipelineWrapper<T, SQLException, Pipeline<T, SQLException>> {

	public JdbcWrapper(@NonNull Pipeline<T, SQLException> source) {
		super(source);
	}
}
