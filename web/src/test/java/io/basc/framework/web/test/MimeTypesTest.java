package io.basc.framework.web.test;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import io.basc.framework.net.MediaTypes;
import io.basc.framework.util.io.MimeTypeUtils;

public class MimeTypesTest {
	@Test
	public void test() {
		MediaTypes mimeTypes = new MediaTypes();
		mimeTypes.add(MimeTypeUtils.APPLICATION_JSON);
		MediaTypes mimeTypes2 = new MediaTypes();
		mimeTypes2.add(MimeTypeUtils.TEXT_JSON);
		assertFalse(mimeTypes.equals(mimeTypes2));
	}
}
