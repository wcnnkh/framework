package scw.configure.convert;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import scw.convert.TypeDescriptor;
import scw.lang.NotSupportedException;
import scw.util.XUtils;

public class PrimaryKeyGetterFactory implements PrimaryKeyGetter, Comparator<PrimaryKeyGetter>{
	protected final TreeSet<PrimaryKeyGetter> primaryKeyGetters = new TreeSet<PrimaryKeyGetter>(this);
	
	public SortedSet<PrimaryKeyGetter> getPrimaryKeyGetters(){
		return XUtils.synchronizedProxy(primaryKeyGetters, this);
	}
	
	public PrimaryKeyGetter getPrimaryKeyGetter(TypeDescriptor sourceType){
		for(PrimaryKeyGetter primaryKeyGetter : primaryKeyGetters){
			if(primaryKeyGetter.matches(sourceType)){
				return primaryKeyGetter;
			}
		}
		return null;
	}
	
	public boolean matches(TypeDescriptor sourceType) {
		return getPrimaryKeyGetter(sourceType) != null;
	}

	public Object get(Object source, TypeDescriptor sourceType) {
		PrimaryKeyGetter primaryKeyGetter = getPrimaryKeyGetter(sourceType);
		if(primaryKeyGetter == null){
			throw new NotSupportedException(sourceType.getType().getName());
		}
		
		return primaryKeyGetter.get(source, sourceType);
	}

	public int compare(PrimaryKeyGetter o1, PrimaryKeyGetter o2) {
		return -1;
	}
}
