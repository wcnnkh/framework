package scw.io.serialzer.hessian;

import java.io.IOException;
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

import scw.aop.ProxyUtils;
import scw.core.utils.ClassUtils;

public final class HessianUtils {
	private static final SerializerFactory SERIALIZER_FACTORY = new SerializerFactory();

	static {
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

	public static void writeProxyObject(Hessian2Output output, Object object) throws IOException {
		if (object == null) {
			output.writeBoolean(false);
		} else {
			Class<?> clazz = object.getClass();
			if (ProxyUtils.getProxyFactory().isProxy(clazz)) {
				output.writeBoolean(true);
				Class<?> userClass = ProxyUtils.getProxyFactory().getUserClass(clazz);
				output.writeString(userClass.getName());
				Class<?>[] interfaces = clazz.getInterfaces();
				int size = interfaces == null ? 0 : interfaces.length;
				output.writeInt(size);
				for (int i = 0; i < size; i++) {
					output.writeString(interfaces[i].getName());
				}
			} else {
				output.writeBoolean(false);
			}
		}
		output.writeObject(object);
	}

	public static Object readProxyObject(Hessian2Input input, ClassLoader classLoader)
			throws IOException, ClassNotFoundException {
		if (input.readBoolean()) {// 代理类
			String className = input.readString();
			Class<?> userClass = ClassUtils.forName(className, classLoader);
			int size = input.readInt();
			Class<?>[] interfaces = new Class<?>[size];
			for (int i = 0; i < size; i++) {
				interfaces[i] = ClassUtils.forName(input.readString(), classLoader);
			}

			return input.readObject(ProxyUtils.getProxyFactory().getProxyClass(userClass, interfaces));
		}
		return input.readObject();
	}

	public static void writeProxyObject(HessianOutput output, Object object) throws IOException {
		if (object == null) {
			output.writeBoolean(false);
		} else {
			Class<?> clazz = object.getClass();
			if (ProxyUtils.getProxyFactory().isProxy(clazz)) {
				output.writeBoolean(true);
				Class<?> userClass = ProxyUtils.getProxyFactory().getUserClass(clazz);
				output.writeString(userClass.getName());
				Class<?>[] interfaces = clazz.getInterfaces();
				int size = interfaces == null ? 0 : interfaces.length;
				output.writeInt(size);
				for (int i = 0; i < size; i++) {
					output.writeString(interfaces[i].getName());
				}
			} else {
				output.writeBoolean(false);
			}
		}
		output.writeObject(object);
	}

	public static Object readProxyObject(HessianInput input, ClassLoader classLoader)
			throws IOException, ClassNotFoundException {
		if (input.readBoolean()) {// 代理类
			String className = input.readString();
			Class<?> userClass = ClassUtils.forName(className, classLoader);
			int size = input.readInt();
			Class<?>[] interfaces = new Class<?>[size];
			for (int i = 0; i < size; i++) {
				interfaces[i] = ClassUtils.forName(input.readString(), classLoader);
			}

			return input.readObject(ProxyUtils.getProxyFactory().getProxyClass(userClass, interfaces));
		}
		return input.readObject();
	}
}
