package scw.mapper;

import java.util.LinkedList;

public class EditableFieldFilters extends LinkedList<FieldFilter> implements
		FieldFilter {
	private static final long serialVersionUID = 1L;

	public boolean accept(Field field) {
		for (FieldFilter filter : this) {
			if (!filter.accept(field)) {
				return false;
			}
		}
		return true;
	}
	
	public void add(FilterFeature filterFeature){
		add(filterFeature.getFilter());
	}
	
	public void addFirst(FilterFeature filterFeature) {
		addFirst(filterFeature.getFilter());
	}
	
	public void addLast(FilterFeature filterFeature) {
		addLast(filterFeature.getFilter());
	}
}
