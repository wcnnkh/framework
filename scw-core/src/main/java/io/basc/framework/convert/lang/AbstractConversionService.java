package io.basc.framework.convert.lang;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.json.JSONSupportAccessor;

public abstract class AbstractConversionService extends JSONSupportAccessor implements ConversionService {
	public String toString() {
		return getClass().getName();
	}
}