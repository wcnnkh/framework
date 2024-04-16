package io.basc.framework.spring.context.test;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;

public class SpringTest {
	public static void main(String[] args) throws IntrospectionException {
		System.out.println(Arrays.toString(Introspector.getBeanInfo(Test4.class).getPropertyDescriptors()));
		
		System.out.println(Arrays.toString(BeanUtils.getPropertyDescriptors(Test1.class)));
		PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(Test1.class, "d");
		System.out.println(propertyDescriptor);
		System.out.println(propertyDescriptor.getReadMethod());
		System.out.println(propertyDescriptor.getWriteMethod());
	}

	public static class Test3 {
		private String a;
	}
	
	@EqualsAndHashCode(callSuper = true)
	public static class Test4 extends Test3{
		public String a;
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class Test1 extends Test2 {
		private String a;

		public String getB() {
			return "b";
		}

		public void setB(String b) {
		}
		
		public String getD() {
			return "d";
		}

		/*
		 * public void setA(Object a) { System.out.println("调用了setA(Object a)"); this.a
		 * = String.valueOf(a); }
		 */

		public Object getA() {
			return "aa";
		}

		/*
		 * public void setA(Test2 a) { System.out.println("调用了setA(Test2 a)"); this.a =
		 * String.valueOf(a); }
		 */

		/*
		 * public void setA(String a) { System.out.println("调用了setA(String a)"); this.a
		 * = a; }
		 */
	}

	@Data
	public static class Test2 {
		private Object a;

		private String c;
	}
}
