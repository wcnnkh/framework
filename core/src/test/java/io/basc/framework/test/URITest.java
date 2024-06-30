package io.basc.framework.test;

import java.net.URI;

import org.junit.Test;

public class URITest {
	@Test
	public void test() {
		String path = "/a/b";
		URI uri = URI.create(path);
		System.out.println(uri);
	}
}
