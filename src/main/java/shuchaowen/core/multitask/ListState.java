package shuchaowen.core.multitask;

import java.util.ArrayList;

public final class ListState<T> extends ArrayList<State<T>>{
	private static final long serialVersionUID = 1L;
	
	public boolean isSuccess(){
		for(State<T> state : this){
			if(state.getThrowable() != null){
				return false;
			}
		}
		return true;
	}
}
