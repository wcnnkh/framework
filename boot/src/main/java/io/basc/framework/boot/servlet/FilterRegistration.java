package io.basc.framework.boot.servlet;

import java.util.Collection;

import javax.servlet.Filter;

public interface FilterRegistration {
	static final String ALL = "/*";

	Filter getFilter();

	Collection<String> getUrlPatterns();
}
