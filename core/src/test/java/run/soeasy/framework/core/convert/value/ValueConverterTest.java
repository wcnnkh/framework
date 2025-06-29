package run.soeasy.framework.core.convert.value;

import java.util.UUID;

import org.junit.Test;

import run.soeasy.framework.core.domain.Value;

public class ValueConverterTest {
	@Test
	public void test() {
		Value value = ValueConverter.DEFAULT.convert(UUID.randomUUID(), Value.class);
		System.out.println(value);
		System.out.println(value.getAsString());
	}
}
