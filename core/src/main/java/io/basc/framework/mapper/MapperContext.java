package io.basc.framework.mapper;

import java.util.function.Predicate;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringMatcher;
import io.basc.framework.util.alias.AliasFactory;

public interface MapperContext {
	StringMatcher getStringMatcher();

	Elements<String> getSourcePatterns();

	AliasFactory getAliasFactory();

	ConversionService getConversionService();

	Predicate<Field> getPredicate();
}
