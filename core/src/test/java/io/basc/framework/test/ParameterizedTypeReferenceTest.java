package io.basc.framework.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.basc.framework.core.ParameterizedTypeReference;

public class ParameterizedTypeReferenceTest {

	public static class A<T> extends ParameterizedTypeReference<T>{
		
		public A() {
			super();
		}
	}
	
	public static class B extends A<String>{
		
	}
	
	@Test
	public void test() {
		assertEquals(new ParameterizedTypeReference<String>() {}.getType(), String.class);
	}
}
