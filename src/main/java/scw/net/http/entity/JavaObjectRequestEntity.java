package scw.net.http.entity;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import scw.net.Request;
import scw.net.RequestEntity;

public class JavaObjectRequestEntity extends ArrayList<Object> implements RequestEntity{
	private static final long serialVersionUID = 1L;
	
	public JavaObjectRequestEntity(){
		super(2);
	}
	
	public void write(Request request) throws IOException {
		if(request.getRequestProperty("Content-Type") == null){
			request.setRequestProperty("Content-Type","application/x-java-serialized-object");
		}
		
		ObjectOutputStream oos = new ObjectOutputStream(request.getOutputStream());
		Iterator<Object> iterator = iterator();
		while(iterator.hasNext()){
			oos.writeObject(iterator.next());
		}
	}
}
