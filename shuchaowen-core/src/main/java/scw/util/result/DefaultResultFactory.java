package scw.util.result;

import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MIN_VALUE)
public class DefaultResultFactory extends AbstractResultFactory {

	public <T> DataResult<T> success(String code, String msg, T data) {
		return new TransactionDataResult<T>(true, code, msg, data, false);
	}

	public <T> DataResult<T> error(String code, String msg, T data) {
		return new TransactionDataResult<T>(false, code, msg, data, true);
	}

}
