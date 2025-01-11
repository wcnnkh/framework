package io.basc.framework.orm;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.data.repository.IndexInfo;
import io.basc.framework.mapper.stereotype.FieldDescriptor;
import io.basc.framework.mapper.stereotype.FieldDescriptorWrapper;
import io.basc.framework.orm.config.Analyzer;
import io.basc.framework.util.MergedElements;
import io.basc.framework.util.Range;
import io.basc.framework.util.collections.Elements;
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

	public DefaultColumnDescriptor(FieldDescriptor field, Class<?> sourceClass, Analyzer analyzer) {
		this(field);
		this.autoIncrement = analyzer.isAutoIncrement(sourceClass, field.setter());
		this.charsetName = analyzer.getCharsetName(sourceClass, field.getter());
		this.comment = analyzer.getCharsetName(sourceClass, field.getter());
		this.entity = analyzer.isEntity(TypeDescriptor.valueOf(sourceClass), field.getter());
		this.increment = analyzer.isIncrement(sourceClass, field.setter());
		this.nullable = analyzer.isNullable(sourceClass, field.setter());
		this.numberRanges = analyzer.getNumberRanges(sourceClass, field.setter());
		this.primaryKey = analyzer.isPrimaryKey(sourceClass, field.getter());
		this.unique = analyzer.isUnique(sourceClass, field.getter());
		this.version = analyzer.isVersion(sourceClass, field.setter());
		this.indexs = analyzer.getIndexs(sourceClass, field.setter());
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

		return new MergedElements<>(this.aliasNames, super.getAliasNames());
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