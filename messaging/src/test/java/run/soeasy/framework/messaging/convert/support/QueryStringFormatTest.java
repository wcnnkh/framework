package run.soeasy.framework.messaging.convert.support;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.soeasy.framework.beans.BeanMapper;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class QueryStringFormatTest {
	@Test
	public void test() {
		BeanMapper.getInstane().getObjectTemplate(QueryStringObject.class).forEach((e) -> {
			System.out.println(e);
		});
		
		Map<String, Object> map = new HashMap<>();
		map.put("value", UUID.randomUUID());
		map.put("name", "query");
		map.put("array", new String[] { "a", "b" });
		String queryString = QueryStringFormat.format(StandardCharsets.UTF_8, map);
		System.out.println("-----------1------------------");
		System.out.println(queryString);
		QueryStringObject queryStringObject = (QueryStringObject) QueryStringFormat.parse(StandardCharsets.UTF_8,
				queryString, TypeDescriptor.valueOf(QueryStringObject.class));
		System.out.println("-----------2------------------");
		System.out.println(queryStringObject);
		System.out.println("-----------3------------------");
		System.out.println(QueryStringFormat.format(StandardCharsets.UTF_8, queryStringObject));
//		assertEquals(queryString, QueryStringFormat.format(StandardCharsets.UTF_8, queryStringObject));
		QueryStringObject newObject = (QueryStringObject) QueryStringFormat.parse(StandardCharsets.UTF_8,
				QueryStringFormat.format(StandardCharsets.UTF_8, queryStringObject),
				TypeDescriptor.valueOf(QueryStringObject.class));
		System.out.println("-----------4------------------");
		System.out.println(newObject);
		assertEquals(queryStringObject, newObject);
	}
	
	@Data
	private static class QueryStringObject1{
		private String[] array;
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class QueryStringObject extends QueryStringObject1{
		private String name;
		private String value;
	}
}
