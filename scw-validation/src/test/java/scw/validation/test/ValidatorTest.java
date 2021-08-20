package scw.validation.test;

import scw.validation.FastValidator;
import scw.validation.test.pojo.ValidatorPojo;

public class ValidatorTest {
	public static void main(String[] args) {
		ValidatorPojo pojo = new ValidatorPojo();
		FastValidator.validate(pojo);
	}
}
