package scw.mapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class SharedFields extends AbstractFields implements Serializable{
	private static final long serialVersionUID = 1L;
	private final Collection<Field> fields;
	
	public SharedFields(Collection<Field> fields){
		this.fields = fields;
	}
	
	public Iterator<Field> iterator() {
		return fields.iterator();
	}
	
	@Override
	public Fields duplicateRemoval() {
		if(fields instanceof Set){
			return new SharedFields(fields);
		}
		return super.duplicateRemoval();
	}
	
	@Override
	public Fields shared() {
		return new SharedFields(fields);
	}
	
	@Override
	public int size() {
		return fields.size();
	}

}
