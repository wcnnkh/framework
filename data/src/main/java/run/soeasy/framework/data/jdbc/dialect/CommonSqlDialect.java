package run.soeasy.framework.data.jdbc.dialect;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.data.jdbc.Sql;

public class CommonSqlDialect implements SqlDialect {

	@Override
	public <E extends PropertyDescriptor> Sql toQueryAllSql(@NonNull TypeDescriptor entityDescriptor,
			Streamable<E> columns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends PropertyAccessor> Sql toDeleteSql(@NonNull TypeDescriptor entityDescriptor,
			@NonNull Streamable<E> columns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends PropertyAccessor> Sql toInsertSql(@NonNull TypeDescriptor entityDescriptor,
			@NonNull Streamable<E> columns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends PropertyAccessor> Sql toQuerySql(@NonNull TypeDescriptor entityDescriptor,
			@NonNull Streamable<E> columns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends PropertyAccessor> Sql toUpdateSql(@NonNull TypeDescriptor entityDescriptor,
			@NonNull Streamable<E> columns) {
		// TODO Auto-generated method stub
		return null;
	}

}
