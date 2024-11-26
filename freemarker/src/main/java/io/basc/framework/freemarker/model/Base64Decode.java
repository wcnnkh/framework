package io.basc.framework.freemarker.model;

import io.basc.framework.freemarker.boot.annotation.SharedVariable;
import io.basc.framework.util.codec.support.Base64;

import java.io.UnsupportedEncodingException;
import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

@SharedVariable
public class Base64Decode implements TemplateMethodModelEx{
	@SuppressWarnings("rawtypes")
	public Object exec(List args) throws TemplateModelException {
		if(args.size() == 0){
			return null;
		}
		
		String str = args.get(0).toString();
		if(str == null){
			return null;
		}
		
		byte[] b = Base64.DEFAULT.decode(str);
		if(b == null){
			return null;
		}
		
		if(args.size() == 1){
			return new String(b);
		}
		
		if(args.size() == 2){
			String charsetName = args.get(1).toString();
			try {
				return new String(b, charsetName);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
