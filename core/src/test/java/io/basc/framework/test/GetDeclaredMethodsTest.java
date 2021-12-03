package io.basc.framework.test;

import org.junit.Test;

import io.basc.framework.util.ObjectUtils;

public class GetDeclaredMethodsTest {
	@Test
	public void test() {
		System.out.println(ObjectUtils.toString(A.class.getDeclaredMethods()));
		System.out.println(ObjectUtils.toString(B.class.getDeclaredMethods()));
		System.out.println(ObjectUtils.toString(B.class.getInterfaces()));
		System.out.println(ObjectUtils.toString(E.class.getDeclaredMethods()));
	}
	
	static interface A{
		void a();
		
		default void aa() {
		}
	}
	
	static interface B extends A{
		void b();
		
		default void bb() {}	
	}
	
	static interface C{
		void c();
		
		default void cc() {};
				
	}
	
	static class D implements C{

		@Override
		public void c() {
		}
		
	}
	
	static class E implements B{

		@Override
		public void a() {
			
		}

		@Override
		public void b() {
		}
		
	}
}
