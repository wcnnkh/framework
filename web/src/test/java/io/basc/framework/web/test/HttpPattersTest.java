package io.basc.framework.web.test;

import org.junit.Test;

import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.util.XUtils;
import io.basc.framework.web.pattern.HttpPattern;
import io.basc.framework.web.pattern.HttpPatterns;

public class HttpPattersTest {
	@Test
	public void already() {
		HttpPatterns<String> httpPatterns = new HttpPatterns<>();
		httpPatterns.add("/a", XUtils.getUUID());
		try {
			httpPatterns.add("/a", XUtils.getUUID());
		} catch (AlreadyExistsException e) {
			return ;
		}
		throw new IllegalAccessError("相同的http pattern验证失败");
	}
	
	@Test
	public void add() {
		HttpPatterns<String> httpPatterns = new HttpPatterns<String>();
		MimeTypes types = new MimeTypes();
		types.add(MimeType.valueOf(MimeTypeUtils.APPLICATION_JSON_VALUE));
		httpPatterns.add(new HttpPattern("/a", "GET", types), XUtils.getUUID());
		
		MimeTypes types2 = new MimeTypes();
		types2.add(MimeTypeUtils.TEXT_JSON);
		httpPatterns.add(new HttpPattern("/a", "GET", types2), XUtils.getUUID());
	}
}
