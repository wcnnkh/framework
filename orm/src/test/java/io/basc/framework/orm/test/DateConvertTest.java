package io.basc.framework.orm.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.annotation.DateFormat;
import io.basc.framework.convert.support.GlobalConversionService;
import io.basc.framework.orm.stereotype.Entity;

public class DateConvertTest {

	@Test
	public void test() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a", "2022-02-04");
		map.put("b", "2022-02-03 00:00:00");

		Target target = new Target();
		target.a = new Date();
		target.b = new java.sql.Date(System.currentTimeMillis());
		Object value = GlobalConversionService.getInstance().convert(target, TypeDescriptor.forObject(target),
				TypeDescriptor.map(Map.class, String.class, String.class));
		System.out.println(value);
	}

	@Entity
	public static class Target {
		@DateFormat(value = "yyyy-MM-dd")
		public Date a;
		@DateFormat(value = "yyyy-MM-dd HH:mm:ss")
		public java.sql.Date b;

	}
}
