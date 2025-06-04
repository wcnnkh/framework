package run.soeasy.framework.core.convert.strings;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

public class StringConverterTest {

	@Test
	public void test() {
		Number value = StringConverter.getInstance().convert("-111", Number.class);
		System.out.println(value.getClass());
		System.out.println(value);
		assertTrue(BigDecimal.class.isAssignableFrom(value.getClass()));
		Assert.assertTrue(-111 == StringConverter.getInstance().convert("-111", int.class));
	}
}
