package io.basc.framework.web.test;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.MimeTypes;

public class MimeTypesTest {
	@Test
	public void test() {
		MimeTypes mimeTypes = new MimeTypes();
		mimeTypes.add(MimeTypeUtils.APPLICATION_JSON);
		MimeTypes mimeTypes2 = new MimeTypes();
		mimeTypes2.add(MimeTypeUtils.TEXT_JSON);
		assertFalse(mimeTypes.equals(mimeTypes2));
	}
}
