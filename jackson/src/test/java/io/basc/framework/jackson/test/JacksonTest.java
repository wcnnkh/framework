package io.basc.framework.jackson.test;

import io.basc.framework.jackson.JacksonJSONSupport;
import io.basc.framework.json.JSONSupport;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class JacksonTest {
	@Test
	public void test(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a", "a1");
		map.put("b", "b2");
		
		JSONSupport jsonSupport = new JacksonJSONSupport();
		System.out.println(jsonSupport.toJSONString(map));
	}
}
