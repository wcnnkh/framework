package scw.util.value.property;

import java.util.Enumeration;

import scw.util.value.ValueFactory;

public interface PropertyFactory extends ValueFactory<String>{
	Enumeration<String> enumerationKeys();
	
	String format(String text, String prefix, String suffix);
	
	String format(String text, boolean supportEL);
}
