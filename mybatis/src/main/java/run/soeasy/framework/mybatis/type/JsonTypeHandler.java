package run.soeasy.framework.mybatis.type;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.jackson.JsonFormat;

/**
 * 默认的json处理
 * 
 * @author soeasy.run
 *
 * @param <S>
 */
public class JsonTypeHandler<S> extends VarcharTypeHandler<S> {

	public JsonTypeHandler() {
		super(JsonFormat.DEFAULT);
	}
	
	public JsonTypeHandler(@NonNull Converter converter) {
		super(converter);
	}
}
