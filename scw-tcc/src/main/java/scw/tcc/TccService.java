package scw.tcc;

import scw.complete.Complete;
import scw.core.reflect.MethodInvoker;
import scw.tcc.annotation.Tcc;

public interface TccService {
	Stage createConfirm(MethodInvoker invoker, Object[] args, Object tryResult, Tcc tcc);

	Stage createCancel(MethodInvoker invoker, Object[] args, Object tryResult, Tcc tcc);

	Complete registerComplete(Stage stage) throws Exception;
}
