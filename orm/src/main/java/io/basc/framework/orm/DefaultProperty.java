package io.basc.framework.orm;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.Element;
import io.basc.framework.mapper.Getter;
import io.basc.framework.mapper.Setter;
import io.basc.framework.util.Range;
import io.basc.framework.util.element.Elements;
import lombok.Data;

@Data
public class DefaultProperty implements Property {
	private final Element field;
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

	public DefaultProperty(Element field) {
		this.field = field;
	}

	public DefaultProperty(Property property) {
		this.field = property;
		this.name = property.getName();
		this.aliasNames = property.getAliasNames();
		this.autoIncrement = property.isAutoIncrement();
		this.charsetName = property.getCharsetName();
		this.comment = property.getComment();
		this.entity = property.isEntity();
		this.increment = property.isIncrement();
		this.nullable = property.isNullable();
		this.numberRanges = property.getNumberRanges();
		this.primaryKey = property.isPrimaryKey();
		this.unique = property.isUnique();
		this.version = property.isVersion();
	}

	public DefaultProperty(Element field, Class<?> sourceClass, EntityResolver resolver) {
		this(field);
		this.autoIncrement = field.getSetters().map((e) -> resolver.isAutoIncrement(sourceClass, e))
				.filter((e) -> e != null).anyMatch((e) -> e);
		this.charsetName = field.getGetters().map((e) -> resolver.getCharsetName(sourceClass, e))
				.filter((e) -> e != null).first();
		this.comment = field.getGetters().map((e) -> resolver.getComment(sourceClass, e)).filter((e) -> e != null)
				.first();
		this.entity = field.getSetters().map((e) -> resolver.isEntity(TypeDescriptor.valueOf(sourceClass), e))
				.filter((e) -> e != null).anyMatch((e) -> e);
		this.increment = field.getSetters().map((e) -> resolver.isIncrement(sourceClass, e)).filter((e) -> e != null)
				.anyMatch((e) -> e);
		this.nullable = field.getSetters().map((e) -> resolver.isNullable(sourceClass, e)).filter((e) -> e != null)
				.anyMatch((e) -> e);
		this.numberRanges = field.getSetters().map((e) -> resolver.getNumberRanges(sourceClass, e))
				.filter((e) -> e != null && !e.isEmpty()).first();
		this.primaryKey = field.getGetters().map((e) -> resolver.isPrimaryKey(sourceClass, e)).filter((e) -> e != null)
				.anyMatch((e) -> e)
				|| field.getSetters().map((e) -> resolver.isPrimaryKey(sourceClass, e)).filter((e) -> e != null)
						.anyMatch((e) -> e);
		this.unique = field.getGetters().map((e) -> resolver.isUnique(sourceClass, e)).filter((e) -> e != null)
				.anyMatch((e) -> e)
				|| field.getSetters().map((e) -> resolver.isUnique(sourceClass, e)).filter((e) -> e != null)
						.anyMatch((e) -> e);
		this.version = field.getSetters().map((e) -> resolver.isVersion(sourceClass, e)).filter((e) -> e != null)
				.anyMatch((e) -> e);
	}

	public boolean isNullable() {
		// 主键不可以为空
		return !primaryKey && nullable;
	}

	@Override
	public String getName() {
		return name == null ? field.getName() : name;
	}

	@Override
	public Elements<String> getAliasNames() {
		if (aliasNames == null || aliasNames.isEmpty()) {
			return field.getAliasNames();
		}

		return Elements.concat(this.aliasNames, field.getAliasNames());
	}

	@Override
	public Elements<? extends Range<Double>> getNumberRanges() {
		return numberRanges == null ? Elements.empty() : numberRanges;
	}

	@Override
	public Elements<? extends Getter> getGetters() {
		return field.getGetters();
	}

	@Override
	public Elements<? extends Setter> getSetters() {
		return field.getSetters();
	}
}