package scw.mapper;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import scw.util.Accept;

public class FieldsWrapper implements Fields {
	private final Fields fields;

	public FieldsWrapper(Fields fields) {
		this.fields = fields;
	}

	public Iterator<Field> iterator() {
		return fields.iterator();
	}

	public Field find(String name, Type type) {
		return fields.find(name, type);
	}

	public Field findGetter(String name, Type type) {
		return fields.findGetter(name, type);
	}

	public Field findSetter(String name, Type type) {
		return fields.findSetter(name, type);
	}

	public Fields accept(Accept<Field> accept) {
		return fields.accept(accept);
	}

	public Fields duplicateRemoval() {
		return fields.duplicateRemoval();
	}

	public Fields shared() {
		return fields.shared();
	}

	public Field first() {
		return fields.first();
	}

	public int size() {
		return fields.size();
	}

	public Map<String, Object> getValueMap(Object instance) {
		return fields.getValueMap(instance);
	}

	public Fields exclude(Accept<Field> accept) {
		return fields.exclude(accept);
	}
	
	public Fields exclude(Collection<String> names) {
		return fields.exclude(names);
	}

	public Map<String, Object> getValueMap(Object instance, boolean nullable) {
		return fields.getValueMap(instance, nullable);
	}
}
