package shuchaowen.core.db.excepion;

/**
 * 数据库更新的行数不满足要求
 * @author shuchaowen
 *
 */
public class UpdateCountException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public UpdateCountException(Throwable e){
		super(e);
	}
	
	public UpdateCountException(String msg){
		super(msg);
	}
	
	public UpdateCountException(String msg, Throwable e){
		super(msg, e);
	}
}
