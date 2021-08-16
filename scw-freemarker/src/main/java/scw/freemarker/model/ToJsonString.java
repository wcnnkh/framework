package scw.freemarker.model;

import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import scw.freemarker.annotation.SharedVariable;
import scw.json.JSONUtils;

@SharedVariable
public class ToJsonString implements TemplateMethodModelEx {
	@SuppressWarnings("rawtypes")
	public Object exec(List args) throws TemplateModelException {
		if (args == null || args.size() == 0) {
			return null;
		}

		Object value = args.get(0);
		if (value == null) {
			return null;
		}
		
		return JSONUtils.getJsonSupport().toJSONString(value);
	}

}