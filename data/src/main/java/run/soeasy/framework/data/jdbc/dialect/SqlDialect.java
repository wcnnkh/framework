package run.soeasy.framework.data.jdbc.dialect;

import lombok.NonNull;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.data.jdbc.Sql;
import run.soeasy.framework.data.orm.EntityDescriptor;

public interface SqlDialect {
	<E extends PropertyDescriptor> Sql toQueryAllSql(@NonNull EntityDescriptor<E> entityDescriptor);

	<E extends PropertyAccessor> Sql toDeleteSql(@NonNull EntityDescriptor<E> entityDescriptor);

	<E extends PropertyAccessor> Sql toInsertSql(@NonNull EntityDescriptor<E> entityDescriptor);

	<E extends PropertyAccessor> Sql toQuerySql(@NonNull EntityDescriptor<E> entityDescriptor);

	<E extends PropertyAccessor> Sql toUpdateSql(@NonNull EntityDescriptor<E> entityDescriptor);
}
