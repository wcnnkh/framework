package run.soeasy.framework.jdbc;

import java.sql.SQLException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.stream.Source;
import run.soeasy.framework.core.function.stream.Source.PipelineWrapper;

@Getter
@RequiredArgsConstructor
public class JdbcWrapper<T> implements PipelineWrapper<T, SQLException, Source<T, SQLException>> {
	@NonNull
	private final Source<T, SQLException> source;
}
