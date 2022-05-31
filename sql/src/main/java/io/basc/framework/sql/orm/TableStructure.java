package io.basc.framework.sql.orm;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import io.basc.framework.mapper.AccessibleField;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Fields;
import io.basc.framework.orm.ObjectRelational;
import io.basc.framework.orm.ObjectRelationalDecorator;
import io.basc.framework.orm.ObjectRelationalResolver;
import io.basc.framework.orm.Property;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;

public final class TableStructure extends ObjectRelationalDecorator<Column, TableStructure> {
	private String engine;
	private String rowFormat;
	private Boolean autoCreate;

	public TableStructure(Class<?> sourceClass, ObjectRelationalResolver objectRelationalResolver, Column parent) {
		this(sourceClass, objectRelationalResolver, parent, Fields.DEFAULT);
	}

	public TableStructure(Class<?> sourceClass, ObjectRelationalResolver objectRelationalResolver, Column parent,
			Function<Class<?>, ? extends Stream<? extends AccessibleField>> processor) {
		super(sourceClass, objectRelationalResolver,
				(e) -> processor.apply(e)
						.filter((o) -> (o.isSupportGetter() && !Modifier.isStatic(o.getGetter().getModifiers()))
								|| (o.isSupportSetter() && !Modifier.isStatic(o.getSetter().getModifiers())))
						.map((o) -> new Field(parent, sourceClass, o))
						.map((o) -> new Property(o, objectRelationalResolver)).map((o) -> new Column(o)));
	}

	public TableStructure(ObjectRelational<Column> members) {
		super(members);
	}

	public Map<IndexInfo, List<Column>> getIndexGroups() {
		Map<IndexInfo, List<Column>> groups = new LinkedHashMap<>();
		columns().forEach((column) -> {
			Collection<IndexInfo> indexs = column.getIndexs();
			if (!CollectionUtils.isEmpty(indexs)) {
				for (IndexInfo indexInfo : indexs) {
					List<Column> columns = groups.get(indexInfo);
					if (columns == null) {
						columns = new ArrayList<>(8);
					}
					columns.add(column);
					groups.put(indexInfo, columns);
				}
			}
		});
		return groups;
	}

	public String getEngine() {
		if (StringUtils.isEmpty(engine) && this.objectRelationalResolver != null) {
			if (objectRelationalResolver instanceof TableResolver) {
				return ((TableResolver) objectRelationalResolver).getEngine(getSourceClass());
			}
		}
		return this.engine;
	}

	public String getRowFormat() {
		if (StringUtils.isEmpty(rowFormat) && this.objectRelationalResolver != null) {
			if (objectRelationalResolver instanceof TableResolver) {
				return ((TableResolver) objectRelationalResolver).getRowFormat(getSourceClass());
			}
		}
		return this.rowFormat;
	}

	@Override
	protected TableStructure decorate(ObjectRelational<Column> members) {
		if (members instanceof TableStructure) {
			return (TableStructure) members;
		}

		TableStructure tableStructure = new TableStructure(members);
		tableStructure.engine = this.engine;
		tableStructure.rowFormat = this.rowFormat;
		return tableStructure;
	}

	public TableStructure setParentProperty(Property property) {
		if (property == null) {
			return setParent(null);
		}

		if (property instanceof Column) {
			return setParent((Column) property);
		}

		Column column = new Column(property);
		return setParent(column);
	}

	@Override
	public TableStructure setParentField(Field field) {
		if (field == null) {
			return setParent(null);
		}

		if (field instanceof Property) {
			return setParentProperty((Property) field);
		}

		Property property = new Property(field, objectRelationalResolver);
		return setParentProperty(property);
	}

	public boolean isAutoCreate() {
		if (autoCreate == null && objectRelationalResolver != null) {
			if (objectRelationalResolver instanceof TableResolver) {
				Boolean v = ((TableResolver) objectRelationalResolver).isAutoCreate(getSourceClass());
				if(v != null) {
					return v;
				}
			}
		}
		return autoCreate == null ? false : true;
	}
}
