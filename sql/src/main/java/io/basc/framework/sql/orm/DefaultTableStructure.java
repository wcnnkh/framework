package io.basc.framework.sql.orm;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.mapper.Fields;
import io.basc.framework.orm.ObjectRelationalResolver;

public class DefaultTableStructure extends TableMetadataWrapper<TableMetadata> implements TableStructure {
	private final ObjectRelationalResolver objectRelationalResolver;
	private final TableStructureProcessor tableStructureProcessor;
	private final Class<?> entityClass;
	private final Fields fields;

	public DefaultTableStructure(ObjectRelationalResolver objectRelationalResolver,
			TableStructureProcessor tableStructureProcessor, Class<?> entityClass, Fields fields) {
		super(tableStructureProcessor.resolveMetadata(entityClass));
		this.objectRelationalResolver = objectRelationalResolver;
		this.tableStructureProcessor = tableStructureProcessor;
		this.entityClass = entityClass;
		this.fields = fields;
	}

	@Override
	public Class<?> getEntityClass() {
		return entityClass;
	}

	@Override
	public List<Column> getProperties() {
		return stream().collect(Collectors.toList());
	}

	@Override
	public Stream<Column> stream() {
		return tableStructureProcessor.map(entityClass, fields);
	}

	public TableStructureProcessor getTableStructureProcessor() {
		return tableStructureProcessor;
	}

	public Fields getFields() {
		return fields;
	}

	@Override
	public Collection<String> getAliasNames() {
		return objectRelationalResolver.getAliasNames(entityClass);
	}
}
