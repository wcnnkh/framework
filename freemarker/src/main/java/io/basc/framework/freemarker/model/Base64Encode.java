package io.basc.framework.freemarker.model;

import io.basc.framework.freemarker.boot.annotation.SharedVariable;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.codec.support.CharsetCodec;

import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

@SharedVariable
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
		
		CharsetCodec codec = CharsetCodec.UTF_8;
		if(args.size() == 2){
			String charsetName = args.get(1).toString();
			if(StringUtils.isNotEmpty(charsetName)){
				codec = new CharsetCodec(charsetName);
			}
		}
		
		return codec.toBase64().encode(str);
	}
}
