package run.soeasy.framework.data.jdbc.dialect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.NonNull;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.data.jdbc.Sql;
import run.soeasy.framework.data.orm.ColumnDescriptor;
import run.soeasy.framework.data.orm.EntityDescriptor;

public class CommonSqlDialect implements SqlDialect {

	@Override
	public <E extends PropertyDescriptor> Sql toQueryAllSql(@NonNull EntityDescriptor<E> entityDescriptor) {
		return new Sql("select * from " + entityDescriptor.getName());
	}

	@Override
	public <E extends PropertyAccessor> Sql toDeleteSql(@NonNull EntityDescriptor<E> entityDescriptor) {
		List<Object> args = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder("select * from " + entityDescriptor.getName() + " where ");
		Iterator<ColumnDescriptor<E>> iterator = entityDescriptor.getColumns().filter((e) -> e.isPrimaryKey()).toList()
				.iterator();
		while (iterator.hasNext()) {
			ColumnDescriptor<E> column = iterator.next();
			sb.append(column.getName()).append("=?");
			args.add(column.getProperty().get());
			if (iterator.hasNext()) {
				sb.append(" and ");
			}
		}
		return new Sql(sb.toString(), args.toArray());
	}

	@Override
	public <E extends PropertyAccessor> Sql toInsertSql(@NonNull EntityDescriptor<E> entityDescriptor) {
		List<Object> args = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder("insert into " + entityDescriptor.getName() + "(");
		Iterator<ColumnDescriptor<E>> iterator = entityDescriptor.getColumns().toList().iterator();
		while (iterator.hasNext()) {
			ColumnDescriptor<E> column = iterator.next();
			sb.append(column.getName());
			args.add(column.getProperty().get());
			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append(") values(");
		Iterator<Object> valueIterator = args.iterator();
		while (valueIterator.hasNext()) {
			valueIterator.next();
			sb.append("?");
			if (valueIterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append(")");
		return new Sql(sb.toString(), args.toArray());
	}

	@Override
	public <E extends PropertyAccessor> Sql toQuerySql(@NonNull EntityDescriptor<E> entityDescriptor) {
		List<Object> args = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder("select * from " + entityDescriptor.getName() + " where ");
		Iterator<ColumnDescriptor<E>> iterator = entityDescriptor.getColumns().filter((e) -> e.isPrimaryKey()).toList()
				.iterator();
		while (iterator.hasNext()) {
			ColumnDescriptor<E> column = iterator.next();
			sb.append(column.getName()).append("=?");
			args.add(column.getProperty().get());
			if (iterator.hasNext()) {
				sb.append(" and ");
			}
		}
		return new Sql(sb.toString(), args.toArray());
	}

	@Override
	public <E extends PropertyAccessor> Sql toUpdateSql(@NonNull EntityDescriptor<E> entityDescriptor) {
		List<Object> args = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder("update " + entityDescriptor.getName() + " set ");
		List<ColumnDescriptor<E>> columns = entityDescriptor.getColumns().toList();
		Iterator<ColumnDescriptor<E>> updateIterator = columns.stream().filter((e) -> !e.isPrimaryKey()).iterator();
		while (updateIterator.hasNext()) {
			ColumnDescriptor<E> column = updateIterator.next();
			sb.append(column.getName()).append("=?");
			args.add(column.getProperty().get());
			if (updateIterator.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append(" where ");
		Iterator<ColumnDescriptor<E>> whereIterator = columns.stream().filter((e) -> e.isPrimaryKey()).iterator();
		while (whereIterator.hasNext()) {
			ColumnDescriptor<E> column = whereIterator.next();
			sb.append(column.getName()).append("=?");
			args.add(column.getProperty().get());
			if (whereIterator.hasNext()) {
				sb.append(" and ");
			}
		}
		return new Sql(sb.toString(), args.toArray());
	}

}
