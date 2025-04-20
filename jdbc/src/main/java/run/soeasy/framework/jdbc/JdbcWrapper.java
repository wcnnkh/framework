package run.soeasy.framework.jdbc;

import java.sql.SQLException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.exe.Pipeline;
import run.soeasy.framework.core.exe.Pipeline.PipelineWrapper;

@Getter
@RequiredArgsConstructor
public class JdbcWrapper<T> implements PipelineWrapper<T, SQLException, Pipeline<T, SQLException>> {
	@NonNull
	private final Pipeline<T, SQLException> source;
}
