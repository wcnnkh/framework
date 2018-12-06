package shuchaowen.core.connection.http.write.object;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import shuchaowen.core.connection.Write;

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
