package io.basc.framework.orm;

import java.util.function.Function;

import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.support.DefaultMapping;
import io.basc.framework.orm.config.Analyzer;
import io.basc.framework.util.Assert;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DefaultEntityMapping<T extends ColumnDescriptor> extends DefaultMapping<T> implements EntityMapping<T> {
	private String comment;
	private String charsetName;

	public DefaultEntityMapping() {
	}

	public <S extends FieldDescriptor> DefaultEntityMapping(Mapping<? extends S> mapping,
			Function<? super S, ? extends T> converter, Class<?> sourceClass, Analyzer analyzer) {
		super(mapping, converter);
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		Assert.requiredArgument(analyzer != null, "relationalResolver");
		setCharsetName(analyzer.getCharsetName(sourceClass));
		setComment(analyzer.getComment(sourceClass));
	}

	public <S extends ColumnDescriptor> DefaultEntityMapping(EntityMapping<? extends S> mapping,
			Function<? super S, ? extends T> converter) {
		super(mapping, converter);
		this.comment = mapping.getComment();
		this.charsetName = mapping.getCharsetName();
	}
}
