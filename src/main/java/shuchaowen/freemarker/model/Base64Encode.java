package shuchaowen.freemarker.model;

import java.io.UnsupportedEncodingException;
import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import shuchaowen.common.utils.Base64;

public class Base64Encode implements TemplateMethodModelEx{

	@SuppressWarnings("rawtypes")
	public Object exec(List args) throws TemplateModelException {
		if(args.size() == 0){
			return null;
		}
		
		String str = args.get(0).toString();
		if(str == null){
			return null;
		}
		
		if(args.size() == 1){
			return Base64.encode(str.getBytes());
		}
		
		if(args.size() == 2){
			String charsetName = args.get(1).toString();
			try {
				return Base64.encode(str.getBytes(charsetName));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
