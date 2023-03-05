package io.basc.framework.freemarker.model;

import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import io.basc.framework.freemarker.boot.annotation.SharedVariable;
import io.basc.framework.json.JsonSupportAccessor;

@SharedVariable
public class ToJsonString extends JsonSupportAccessor implements TemplateMethodModelEx {
	@SuppressWarnings("rawtypes")
	public Object exec(List args) throws TemplateModelException {
		if (args == null || args.size() == 0) {
			return null;
		}

		Object value = args.get(0);
		if (value == null) {
			return null;
		}

		return getJsonSupport().toJsonString(value);
	}

}