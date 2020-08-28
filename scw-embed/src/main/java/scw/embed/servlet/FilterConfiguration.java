package scw.embed.servlet;

import java.util.Collection;

import javax.servlet.Filter;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface FilterConfiguration{
	String getName();
	
	Collection<? extends String> getURLPatterns();
	
	Collection<? extends Filter> getFilters();
}
