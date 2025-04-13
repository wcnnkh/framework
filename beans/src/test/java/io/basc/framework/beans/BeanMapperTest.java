package io.basc.framework.beans;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.soeasy.framework.beans.BeanMapper;
import run.soeasy.framework.sequences.uuid.UUIDSequences;

public class BeanMapperTest {
	@Test
	public void util() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("k", UUIDSequences.global().next());
		map.put("b.bk", UUIDSequences.global().next());
		map.put("bk", "bk");
		map.put("s.a", UUIDSequences.global().next());
		map.put("s.b", UUIDSequences.global().next());
		BeanMapper mapper = new BeanMapper();
		A a = mapper.convert(map, A.class);
		System.out.println(map);
		System.out.println(a);
		assertTrue(map.get("k").equals(a.getK()));
		assertTrue(map.get("b.bk").equals(a.getB().getBk()));
		System.out.println(a);
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class A extends B {
		private int v;
		private String k;
		private B b;
		private Map<String, String> s;
	}

	@Data
	public static class B {
		private String bk;
	}
}
