package shuchaowen.web.db.cache;

public class CASException extends RuntimeException{
	private static final long serialVersionUID = -7469282541619257765L;
	
	public CASException(String key, long cas){
		super(toMsg(key, cas));
	}
	
	private static String toMsg(String key, long cas){
		StringBuilder sb = new StringBuilder();
		sb.append("key=");
		sb.append(key);
		sb.append(",cas=");
		sb.append(cas);
		return sb.toString();
	}
}
