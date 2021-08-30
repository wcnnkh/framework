package io.basc.framework.freemarker.model;

import io.basc.framework.freemarker.annotation.SharedVariable;

import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

@SharedVariable
public class ToString implements TemplateMethodModelEx {
	@SuppressWarnings("rawtypes")
	public Object exec(List args) throws TemplateModelException {
		if (args == null || args.size() == 0) {
			return null;
		}

		Object value = args.get(0);
		if (value == null) {
			return null;
		}

		return String.valueOf(value);
	}

}
