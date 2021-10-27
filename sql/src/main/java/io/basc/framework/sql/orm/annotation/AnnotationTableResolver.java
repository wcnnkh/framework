package io.basc.framework.sql.orm.annotation;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.mapper.FieldFeature;
import io.basc.framework.mapper.Fields;
import io.basc.framework.orm.DefaultObjectRelationalResolver;
import io.basc.framework.sql.orm.Column;
import io.basc.framework.sql.orm.TableResolver;
import io.basc.framework.sql.orm.TableStructure;
import io.basc.framework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnnotationTableResolver extends DefaultObjectRelationalResolver implements TableResolver {

	@Override
	public Fields getFields(Class<?> clazz, Field parentField) {
		return super.getFields(clazz, parentField).entity().accept(FieldFeature.EXISTING_FIELD);
	}

	public Counter getCounter(Field field) {
		return field.getAnnotation(Counter.class);
	}

	public boolean isAutoIncrement(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.isAnnotated(fieldDescriptor, AutoIncrement.class);
	}

	public boolean isAutoIncrement(Field field) {
		return (field.isSupportGetter() && isAutoIncrement(field.getGetter())
				|| (field.isSupportSetter() && isAutoIncrement(field.getSetter())));
	}

	public boolean isUnique(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.isAnnotated(fieldDescriptor, Unique.class);
	}

	public boolean isUnique(Field field) {
		return (field.isSupportGetter() && isUnique(field.getGetter()))
				|| (field.isSupportSetter() && isUnique(field.getSetter()));
	}

	/**
	 * 字段描述
	 * 
	 * @param field
	 * @return
	 */
	@Nullable
	public String getComment(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.getDescription(fieldDescriptor);
	}

	public String getComment(Field field) {
		String desc = getComment(field.getGetter());
		if (desc == null) {
			desc = getComment(field.getSetter());
		}
		return desc;
	}

	public String getCharsetName(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.getCharsetName(fieldDescriptor, null);
	}

	public Index getIndex(Field field) {
		return AnnotatedElementUtils.getMergedAnnotation(field, Index.class);
	}

	@Override
	public TableStructure resolve(Class<?> entityClass) {
		return new EntityTableStructore(entityClass);
	}

	private class EntityTableStructore implements TableStructure {
		private final Class<?> entityClass;
		private volatile List<Column> columns;

		public EntityTableStructore(Class<?> entityClass) {
			this.entityClass = entityClass;
		}

		public java.lang.Class<?> getEntityClass() {
			return entityClass;
		};

		@Override
		public String getName() {
			return AnnotationTableResolver.this.getName(entityClass);
		}

		@Override
		public List<Column> getRows() {
			if (columns == null) {
				synchronized (this) {
					if (columns == null) {
						List<Column> columns = new ArrayList<>();
						for (Field field : getFields(entityClass).all()) {
							if (isEntity(field.getGetter()) || isEntity(field.getSetter())) {
								continue;
							}

							Column column = new FieldColumn(field);
							columns.add(column);
						}
						this.columns = columns;
					}
				}
			}
			return columns;
		}

		@Override
		public Map<String, List<Column>> getIndexGroup() {
			Map<String, List<Column>> indexGroup = new LinkedHashMap<>();
			for (Column column : this) {
				io.basc.framework.sql.orm.annotation.Index index = AnnotationTableResolver.this.getIndex(column.getField());
				if (index == null) {
					continue;
				}

				String indexName = index.name();
				if (StringUtils.isEmpty(indexName)) {
					indexName = column.getName();
				}

				List<Column> list = indexGroup.get(indexName);
				if (list == null) {
					list = new ArrayList<Column>();
					indexGroup.put(indexName, list);
				}
				list.add(column);
			}
			return indexGroup;
		}

	}

	private class FieldColumn implements Column {
		private final Field field;

		public FieldColumn(Field field) {
			this.field = field;
		}

		@Override
		public String getName() {
			return AnnotationTableResolver.this.getName(field.getGetter());
		}

		@Override
		public boolean isPrimaryKey() {
			return AnnotationTableResolver.this.isPrimaryKey(field);
		}

		@Override
		public boolean isAutoIncrement() {
			return AnnotationTableResolver.this.isAutoIncrement(field);
		}

		@Override
		public boolean isNullable() {
			return AnnotationTableResolver.this.isNullable(field);
		}

		@Override
		public boolean isUnique() {
			return AnnotationTableResolver.this.isUnique(field);
		}

		@Override
		public String getCharsetName() {
			return AnnotationTableResolver.this.getCharsetName(field.getGetter());
		}

		@Override
		public String getComment() {
			return AnnotationTableResolver.this.getComment(field);
		}

		@Override
		public Field getField() {
			return field;
		}

	}
}
