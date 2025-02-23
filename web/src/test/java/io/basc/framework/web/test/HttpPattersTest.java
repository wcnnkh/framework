package io.basc.framework.web.test;

import org.junit.Test;

import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.net.MediaTypes;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.io.MimeType;
import io.basc.framework.util.io.MimeTypeUtils;
import io.basc.framework.web.pattern.HttpPattern;
import io.basc.framework.web.pattern.HttpPatternMatcher;

public class HttpPattersTest {
	@Test
	public void already() {
		HttpPatternMatcher<String> httpPatterns = new HttpPatternMatcher<>();
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
		HttpPatternMatcher<String> httpPatterns = new HttpPatternMatcher<String>();
		MediaTypes types = new MediaTypes();
		types.add(MimeType.valueOf(MimeTypeUtils.APPLICATION_JSON_VALUE));
		httpPatterns.add(new HttpPattern("/a", "GET", types), XUtils.getUUID());
		
		MediaTypes types2 = new MediaTypes();
		types2.add(MimeTypeUtils.TEXT_JSON);
		httpPatterns.add(new HttpPattern("/a", "GET", types2), XUtils.getUUID());
	}
}
