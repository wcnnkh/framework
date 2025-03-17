package io.basc.framework.web.test;

import org.junit.Test;

import run.soeasy.framework.lang.AlreadyExistsException;
import run.soeasy.framework.net.MediaTypes;
import run.soeasy.framework.util.XUtils;
import run.soeasy.framework.util.io.MimeType;
import run.soeasy.framework.util.io.MimeTypeUtils;
import run.soeasy.framework.web.pattern.HttpPattern;
import run.soeasy.framework.web.pattern.HttpPatternMatcher;

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
