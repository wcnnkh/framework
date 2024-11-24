package io.basc.framework.core.convert.config;

import java.util.Set;

import io.basc.framework.core.convert.ConvertiblePair;

public interface ConvertibleConditional {
	Set<ConvertiblePair> getConvertibleTypes();
}
