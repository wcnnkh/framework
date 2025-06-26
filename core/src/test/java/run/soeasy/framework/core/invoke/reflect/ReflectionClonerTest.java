package run.soeasy.framework.core.invoke.reflect;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import lombok.ToString;
import run.soeasy.framework.core.transform.property.Cloner;
import run.soeasy.framework.sequences.uuid.UUIDSequences;
import run.soeasy.framework.serializer.SerializerException;

public class ReflectionClonerTest {
	@Test
	public void list() throws ClassNotFoundException, SerializerException {
		TestList bean = new TestList();
		bean.add(UUIDSequences.global().next());
		bean.setV(UUIDSequences.global().next());
		System.out.println(bean);
		TestList clone = Cloner.clone(bean, false);
		System.out.println(clone);
		Assert.assertTrue(clone.getV().equals(bean.getV()) && clone.size() == 1);
	}

	@Test
	public void map() {
		TestMap bean = new TestMap();
		bean.put(UUIDSequences.global().next(), UUIDSequences.global().next());
		bean.setV(UUIDSequences.global().next());
		System.out.println(bean);
		TestMap clone = Cloner.clone(bean, false);
		System.out.println(clone);
		Assert.assertTrue(clone.getV().equals(bean.getV()) && clone.size() == 1);
	}

	@ToString(callSuper = true)
	private static class TestList extends LinkedList<Object> {
		private static final long serialVersionUID = 1L;
		private String v;

		public String getV() {
			return v;
		}

		public void setV(String v) {
			this.v = v;
		}
	}

	@ToString(callSuper = true)
	private static class TestMap extends HashMap<Object, Object> {
		private static final long serialVersionUID = 1L;
		private String v;

		public String getV() {
			return v;
		}

		public void setV(String v) {
			this.v = v;
		}
	}
}
