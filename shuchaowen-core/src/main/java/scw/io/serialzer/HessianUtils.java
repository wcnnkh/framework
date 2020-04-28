package scw.io.serialzer;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import com.caucho.hessian.io.BigDecimalDeserializer;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.io.StringValueSerializer;

public final class HessianUtils {
	private static final SerializerFactory SERIALIZER_FACTORY;

	static {
		SERIALIZER_FACTORY = new SerializerFactory();
		SERIALIZER_FACTORY.addFactory(new HessianAddSerializerFactory(BigDecimal.class, new StringValueSerializer(),
				new BigDecimalDeserializer()));
	}

	public static SerializerFactory getSerializerFactory() {
		return SERIALIZER_FACTORY;
	}

	public static Hessian2Output createHessian2Output(OutputStream out) {
		Hessian2Output output = new Hessian2Output(out);
		output.setSerializerFactory(SERIALIZER_FACTORY);
		return output;
	}

	public static Hessian2Input createHessian2Input(InputStream input) {
		Hessian2Input hi = new Hessian2Input(input);
		hi.setSerializerFactory(SERIALIZER_FACTORY);
		return hi;
	}

	public static HessianOutput createHessianOutput(OutputStream out) {
		HessianOutput output = new HessianOutput(out);
		output.setSerializerFactory(SERIALIZER_FACTORY);
		return output;
	}

	public static HessianInput createHessianInput(InputStream input) {
		HessianInput in = new HessianInput(input);
		in.setSerializerFactory(SERIALIZER_FACTORY);
		return in;
	};
}
