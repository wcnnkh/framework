package scw.transaction.tcc;

import scw.aop.ProxyContext;
import scw.complete.Complete;
import scw.transaction.tcc.annotation.Tcc;

public interface TccService {
	Stage createConfirm(ProxyContext context, Object tryResult, Tcc tcc);

	Stage createCancel(ProxyContext context, Object tryResult, Tcc tcc);

	Complete registerComplete(Stage stage) throws Exception;
}
