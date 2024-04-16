package io.basc.framework.orm;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.repository.IndexInfo;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.mapper.FieldDescriptorWrapper;
import io.basc.framework.util.Range;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DefaultColumnDescriptor extends FieldDescriptorWrapper<FieldDescriptor> implements ColumnDescriptor {
	private String name;
	private Elements<String> aliasNames;
	private boolean autoIncrement;
	private String charsetName;
	private String comment;
	private boolean entity;
	private boolean increment;
	private boolean nullable;
	private Elements<? extends Range<Double>> numberRanges;
	private boolean primaryKey;
	private boolean unique;
	private boolean version;
	private Elements<IndexInfo> indexs;

	public DefaultColumnDescriptor(FieldDescriptor fieldDescriptor) {
		super(fieldDescriptor);
	}

	public DefaultColumnDescriptor(ColumnDescriptor columnDescriptor) {
		super(columnDescriptor);
		this.name = columnDescriptor.getName();
		this.aliasNames = columnDescriptor.getAliasNames();
		this.autoIncrement = columnDescriptor.isAutoIncrement();
		this.charsetName = columnDescriptor.getCharsetName();
		this.comment = columnDescriptor.getComment();
		this.entity = columnDescriptor.isEntity();
		this.increment = columnDescriptor.isIncrement();
		this.nullable = columnDescriptor.isNullable();
		this.numberRanges = columnDescriptor.getNumberRanges();
		this.primaryKey = columnDescriptor.isPrimaryKey();
		this.unique = columnDescriptor.isUnique();
		this.version = columnDescriptor.isVersion();
		this.indexs = columnDescriptor.getIndexs();
	}

	public DefaultColumnDescriptor(FieldDescriptor field, Class<?> sourceClass, EntityResolver resolver) {
		this(field);
		this.autoIncrement = resolver.isAutoIncrement(sourceClass, field.setter());
		this.charsetName = resolver.getCharsetName(sourceClass, field.getter());
		this.comment = resolver.getCharsetName(sourceClass, field.getter());
		this.entity = resolver.isEntity(TypeDescriptor.valueOf(sourceClass), field.getter());
		this.increment = resolver.isIncrement(sourceClass, field.setter());
		this.nullable = resolver.isNullable(sourceClass, field.setter());
		this.numberRanges = resolver.getNumberRanges(sourceClass, field.setter());
		this.primaryKey = resolver.isPrimaryKey(sourceClass, field.getter());
		this.unique = resolver.isUnique(sourceClass, field.getter());
		this.version = resolver.isVersion(sourceClass, field.setter());
		this.indexs = resolver.getIndexs(sourceClass, field.setter());
	}

	public boolean isNullable() {
		// 主键不可以为空
		return !primaryKey && nullable;
	}

	@Override
	public String getName() {
		return name == null ? super.getName() : name;
	}

	@Override
	public Elements<String> getAliasNames() {
		if (aliasNames == null || aliasNames.isEmpty()) {
			return super.getAliasNames();
		}

		return Elements.concat(this.aliasNames, super.getAliasNames());
	}

	@Override
	public Elements<? extends Range<Double>> getNumberRanges() {
		return numberRanges == null ? Elements.empty() : numberRanges;
	}

	@Override
	public Elements<IndexInfo> getIndexs() {
		return indexs == null ? Elements.empty() : indexs;
	}

}