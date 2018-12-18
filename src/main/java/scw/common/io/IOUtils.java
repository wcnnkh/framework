package scw.common.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import scw.common.utils.XUtils;

public final class IOUtils {
	private IOUtils(){};
	
	public static void write(OutputStream os, InputStream is, int buffSize) throws IOException{
		byte[] b = new byte[buffSize];
		int len = 0;
		while((len = is.read(b)) != -1){
			os.write(b, 0, len);
		}
	}
	
	public static byte[] javaObjectToByte(Object obj) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			throw e;
		}finally{
			XUtils.close(oos, bos);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T byteToJavaObject(byte[] buf) throws ClassNotFoundException, IOException{
		ByteArrayInputStream bis = new ByteArrayInputStream(buf);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bis);
			return (T) ois.readObject();
		} catch (IOException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			throw e;
		}finally{
			XUtils.close(ois, bis);
		}
	}
}
