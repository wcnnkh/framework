package scw.hibernate.test;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;


public class ValidatorTest {
	public static void main(String[] args) {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
	}
}
