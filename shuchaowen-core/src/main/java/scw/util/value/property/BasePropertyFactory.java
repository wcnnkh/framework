package scw.util.value.property;

import java.util.Enumeration;

import scw.util.value.BaseValueFactory;

public interface BasePropertyFactory extends BaseValueFactory<String> {
	Enumeration<String> enumerationKeys();
}
