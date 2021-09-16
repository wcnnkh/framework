package io.basc.framework.freemarker.model;

import java.util.List;

import freemarker.template.SimpleDate;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import io.basc.framework.freemarker.annotation.SharedVariable;
import io.basc.framework.util.TimeUtils;

@SharedVariable
public class DateFormat implements TemplateMethodModelEx{

	@SuppressWarnings("rawtypes")
	public Object exec(List args) throws TemplateModelException {
		if(args.size() != 2){
			return null;
		}
		
		Object obj = args.get(0);
		String format = args.get(1).toString();
		if(obj == null || format == null){
			return null;
		}
		
		if(SimpleNumber.class.isAssignableFrom(obj.getClass())){
			return TimeUtils.format(((SimpleNumber)obj).getAsNumber().longValue(), format);
		}else if(SimpleDate.class.isAssignableFrom(obj.getClass())){
			return TimeUtils.format(((SimpleDate)obj).getAsDate(), format);
		}else{
			return TimeUtils.parse(obj.toString(), format);
		}
	}
}
