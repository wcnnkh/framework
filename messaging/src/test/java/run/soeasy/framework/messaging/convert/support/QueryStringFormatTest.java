package run.soeasy.framework.messaging.convert.support;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import lombok.Data;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class QueryStringFormatTest {
	@Test
	public void test() {
		Map<String, Object> map = new HashMap<>();
		map.put("value", UUID.randomUUID());
		map.put("name", "query");
//		map.put("array", "a");
//		map.put("array", "b");
		String queryString = QueryStringFormat.format(StandardCharsets.UTF_8, map);
		System.out.println(queryString);
		QueryStringObject queryStringObject = (QueryStringObject) QueryStringFormat.parse(StandardCharsets.UTF_8,
				queryString, TypeDescriptor.valueOf(QueryStringObject.class));
		System.out.println(queryStringObject);
		
	}

	@Data
	public static class QueryStringObject {
		private String name;
		private String value;
	//	private String[] array;
	}
}
