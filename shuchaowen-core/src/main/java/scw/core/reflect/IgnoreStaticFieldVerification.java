package scw.core.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import scw.core.Verification;

public class IgnoreStaticFieldVerification implements Verification<Field> {
	private boolean ignoreTransient;
	
	public IgnoreStaticFieldVerification(boolean ignoreTransient){
		this.ignoreTransient = ignoreTransient;
	}
	
	public boolean verification(Field data) {
		if (data == null) {
			return true;
		}

		if(Modifier.isStatic(data.getModifiers())){
			return true;
		}
		
		if(ignoreTransient && Modifier.isTransient(data.getModifiers())){
			return true;
		}
		
		return false;
	}

}
