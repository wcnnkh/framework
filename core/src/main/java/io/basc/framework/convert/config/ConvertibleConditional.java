package io.basc.framework.convert.config;

import java.util.Set;

import io.basc.framework.convert.ConvertiblePair;

public interface ConvertibleConditional {
	Set<ConvertiblePair> getConvertibleTypes();
}
