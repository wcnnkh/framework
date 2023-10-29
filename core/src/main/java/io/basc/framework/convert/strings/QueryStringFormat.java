package io.basc.framework.convert.strings;

import java.io.Reader;
import java.io.Writer;

import io.basc.framework.util.collect.MultiValueMap;

public interface QueryStringFormat {
	void read(Reader source, MultiValueMap<String, String> target);

	void write(MultiValueMap<String, String> source, Writer target);
}
