package scw.convert.lang;

import scw.convert.ConversionService;
import scw.json.JSONSupportAccessor;

public abstract class AbstractConversionService extends JSONSupportAccessor implements ConversionService {
	public String toString() {
		return getClass().getName();
	}
}