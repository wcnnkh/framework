package io.basc.framework.orm;

import java.util.function.Function;

import io.basc.framework.mapper.Element;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.support.DefaultMapping;
import io.basc.framework.util.Assert;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DefaultEntityMapping<T extends Property> extends DefaultMapping<T> implements EntityMapping<T> {
	private String comment;
	private String charsetName;

	public DefaultEntityMapping() {
	}

	public <S extends Element> DefaultEntityMapping(Mapping<? extends S> mapping,
			Function<? super S, ? extends T> converter, Class<?> sourceClass,
			EntityResolver relationalResolver) {
		super(mapping, converter);
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		Assert.requiredArgument(relationalResolver != null, "relationalResolver");
		setCharsetName(relationalResolver.getCharsetName(sourceClass));
		setComment(relationalResolver.getComment(sourceClass));
	}

	public <S extends Property> DefaultEntityMapping(EntityMapping<? extends S> mapping,
			Function<? super S, ? extends T> converter) {
		super(mapping, converter);
		this.comment = mapping.getComment();
		this.charsetName = mapping.getCharsetName();
	}
}
