package run.soeasy.framework.core.convert.support;

import org.junit.Test;

public class SystemConversionServiceTest {
	@Test
	public void test() {
		System.out.println(SystemConversionService.getInstance().canConvert(Object.class, Integer.class));
	}
}
