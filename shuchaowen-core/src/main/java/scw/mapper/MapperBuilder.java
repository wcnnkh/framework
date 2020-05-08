package scw.mapper;

import java.util.Enumeration;
import java.util.LinkedList;

public class MapperBuilder{
	private LinkedList<FieldFilter> fieldFilters;
	private boolean useSuperClass;
	
	public LinkedList<FieldFilter> getFieldFilters() {
		return fieldFilters;
	}

	public void setFieldFilters(LinkedList<FieldFilter> fieldFilters) {
		this.fieldFilters = fieldFilters;
	}

	public boolean isUseSuperClass() {
		return useSuperClass;
	}

	public void setUseSuperClass(boolean useSuperClass) {
		this.useSuperClass = useSuperClass;
	}
	
	public Enumeration<Field> enumeration(Class<?> entityClass){
		return new EnumerationField();
	}
	
	private class EnumerationField implements Enumeration<Field>{
		private Class<?> entityClass;
		
		
		public boolean hasMoreElements() {
			// TODO Auto-generated method stub
			return false;
		}

		public Field nextElement() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
