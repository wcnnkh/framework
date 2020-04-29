package scw.io.serialzer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

import scw.aop.ProxyUtils;

public class ProxyObjectOutputStream extends ObjectOutputStream {

	public ProxyObjectOutputStream(OutputStream out) throws IOException {
		super(out);
	}

	@Override
	protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
		Class<?> cl = desc.forClass();
		if (ProxyUtils.getProxyFactory().isProxy(cl)) {
			writeBoolean(true);
			Class<?> superClass = ProxyUtils.getProxyFactory().getUserClass(cl);
			writeObject(superClass.getName());
			Class<?>[] interfaces = cl.getInterfaces();
			int size = interfaces == null ? 0 : interfaces.length;
			writeInt(size);
			for (int i = 0; i < size; i++) {
				writeObject(interfaces[i].getName());
			}
		} else {
			writeBoolean(false);
			super.writeClassDescriptor(desc);
		}
	}
}
