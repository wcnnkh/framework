package io.basc.framework.validation.test;

import io.basc.framework.validation.FastValidator;
import io.basc.framework.validation.test.pojo.ValidatorPojo;

public class ValidatorTest {
	public static void main(String[] args) {
		ValidatorPojo pojo = new ValidatorPojo();
		FastValidator.validate(pojo);
	}
}
