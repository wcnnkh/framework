package scw.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import scw.codec.support.CharsetCodec;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;

public class Md5Test {
	@Test
	public void test() {
		String token = "alslfngodkfkfklnlaasdfajsdfwjaaddsidwiwrifk";
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("car_no", "沪A123456");
		map.put("timestamp", "1620372200727");
		map.put("channel", "4001");
		
		map = CollectionUtils.sort(map);
		System.out.println(map);
		String content = StringUtils.collectionToDelimitedString(map.values(), "");
		System.out.println(content);
		String sign = CharsetCodec.UTF_8.toMD5().encode(content + token);
		System.out.println(sign);
	}
}
