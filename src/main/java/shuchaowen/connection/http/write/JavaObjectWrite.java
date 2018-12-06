package shuchaowen.connection.http.write;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import shuchaowen.connection.Write;

public class JavaObjectWrite implements Write{
	private final Object data;
	
	public JavaObjectWrite(Object data){
		this.data = data;
	}
	
	public void write(OutputStream outputStream) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(outputStream);
		oos.writeObject(data);
	}

}
