package io.basc.framework.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.basc.framework.util.placeholder.support.SmartPlaceholderReplacer;

public class PlaceholderReplacerTest {
	@Test
	public void test() {
		Map<String, Object> params = new HashMap<>();
		params.put("a", "111");
		params.put("b", "222");
		
		String res = SmartPlaceholderReplacer.NON_STRICT_REPLACER.replacePlaceholders("${a}-${b}", params);
		System.out.println(res);
		
		res = SmartPlaceholderReplacer.NON_STRICT_REPLACER.replacePlaceholders("${a}-${c}", params);
		System.out.println(res);
		
		try {
			res = SmartPlaceholderReplacer.NON_STRICT_REPLACER.replaceRequiredPlaceholders("${a}-${c}", params);
			throw new RuntimeException("不应该到这里");
		} catch (IllegalArgumentException e) {
		}
		
		res = SmartPlaceholderReplacer.NON_STRICT_REPLACER.replaceRequiredPlaceholders("${a}-${c:222}", params);
		System.out.println(res);
	}
		
}
