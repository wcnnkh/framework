package scw.value.property;

import java.util.Enumeration;

import scw.value.BaseValueFactory;

public interface BasePropertyFactory extends BaseValueFactory<String> {
	Enumeration<String> enumerationKeys();
}
