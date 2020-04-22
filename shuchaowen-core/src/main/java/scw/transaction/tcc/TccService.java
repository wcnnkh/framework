package scw.transaction.tcc;

import java.lang.reflect.Method;

import scw.transaction.tcc.annotation.Tcc;

public interface TccService {
	Stage createConfirm(Class<?> targetClass, Method tryMethod, Object tryResult, Object[] args, Tcc tcc);

	Stage createCancel(Class<?> targetClass, Method tryMethod, Object tryResult, Object[] args, Tcc tcc);

	void execute(Stage stage);
}
