package io.basc.framework.sql.orm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.basc.framework.core.Structure;
import io.basc.framework.mapper.AccessibleField;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Fields;
import io.basc.framework.orm.ObjectRelational;
import io.basc.framework.orm.ObjectRelationalDecorator;
import io.basc.framework.orm.ObjectRelationalFactory;
import io.basc.framework.orm.ObjectRelationalResolver;
import io.basc.framework.orm.Property;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;

public final class TableStructure extends ObjectRelationalDecorator<Column, TableStructure> {
	private String engine;
	private String rowFormat;
	private Boolean autoCreate;

	public TableStructure(Class<?> sourceClass, ObjectRelationalResolver objectRelationalResolver, Column parent) {
		this(sourceClass, objectRelationalResolver, parent, Fields.DEFAULT);
	}

	public TableStructure(Class<?> sourceClass, ObjectRelationalResolver objectRelationalResolver, Column parent,
			Function<Class<?>, ? extends Elements<? extends AccessibleField>> processor) {
		super(sourceClass, objectRelationalResolver, parent,
				new ColumnsFunction(objectRelationalResolver, parent, processor));
	}

	public TableStructure(Structure<Column> members) {
		super(members);
		if (members instanceof TableStructure) {
			this.engine = ((TableStructure) members).engine;
			this.rowFormat = ((TableStructure) members).rowFormat;
			this.autoCreate = ((TableStructure) members).autoCreate;
		}
	}

	public TableStructure(Structure<? extends Field> members, Function<? super Field, ? extends Column> map) {
		super(members, (e) -> {
			if (e == null) {
				return null;
			}

			if (e instanceof Column) {
				return (Column) e;
			}
			return map.apply(e);
		});
		if (members instanceof TableStructure) {
			this.engine = ((TableStructure) members).engine;
			this.rowFormat = ((TableStructure) members).rowFormat;
			this.autoCreate = ((TableStructure) members).autoCreate;
		}
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

	@Override
	public TableStructure jumpTo(Class<?> cursorId) {
		if (objectRelationalResolver != null && objectRelationalResolver instanceof TableMapper) {
			return ((TableMapper) objectRelationalResolver).getStructure(cursorId);
		}

		if (objectRelationalResolver != null && objectRelationalResolver instanceof ObjectRelationalFactory) {
			ObjectRelational<? extends Property> objectRelational = ((ObjectRelationalFactory) objectRelationalResolver)
					.getStructure(cursorId);
			return new TableStructure(objectRelational, (e) -> new Column((Property) e));
		}
		return super.jumpTo(cursorId);
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
				if (v != null) {
					return v;
				}
			}
		}
		return autoCreate == null ? false : true;
	}

	@Override
	protected Column clone(Column source) {
		return source.clone();
	}
}
