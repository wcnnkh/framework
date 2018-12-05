package shuchaowen.web.servlet.view.result;

public class ObjectResult extends DataResult<Object>{
	private static final long serialVersionUID = 1L;
	
	public static ObjectResult success(Object data){
		ObjectResult result = new ObjectResult();
		result.setData(data);
		return result;
	}
}
