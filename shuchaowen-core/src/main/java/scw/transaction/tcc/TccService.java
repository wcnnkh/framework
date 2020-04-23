package scw.transaction.tcc;

import scw.aop.Context;
import scw.transaction.tcc.annotation.Tcc;

public interface TccService {
	Stage createConfirm(Context context, Object tryResult, Tcc tcc);

	Stage createCancel(Context context, Object tryResult, Tcc tcc);

	void execute(Stage stage);
}
