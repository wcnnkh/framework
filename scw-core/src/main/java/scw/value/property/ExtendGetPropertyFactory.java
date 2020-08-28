package scw.value.property;

import scw.value.AnyValue;
import scw.value.Value;

public abstract class ExtendGetPropertyFactory extends PropertyFactory {

	public ExtendGetPropertyFactory(boolean concurrent, boolean priorityOfUseSelf) {
		super(concurrent, priorityOfUseSelf);
	}
	
	@Override
	public final Value get(String key) {
		if(isPriorityOfUseSelf()){
			Object value = getExtendValue(key);
			if (value != null) {
				return new AnyValue(value, getDefaultValue(key));
			}
			return super.get(key);
		}else{
			Value value = super.get(key);
			if(value == null){
				Object v = getExtendValue(key);
				if (v != null) {
					value = new AnyValue(v, getDefaultValue(key));
				}
			}
			return value;
		}
	}
	
	@Override
	public boolean containsKey(String key) {
		return super.containsKey(key) || getExtendValue(key) != null;
	}

	protected abstract Object getExtendValue(String key);
}
