package shuchaowen.common.utils;

public final class CollectionUtils {
	private CollectionUtils(){};
	
	public static char[] reversedCharArray(char[] array){
		if(array == null){
			return array;
		}
		
		char[] newArray = new char[array.length];
		int index = 0;
		for(int i=newArray.length - 1; i>=0; i--, index ++){
			newArray[index] = array[i];
		}
		return newArray;
	}
	
	public static char[] mergeCharArray(char[] ...chars){
		StringBuilder sb = new StringBuilder();
		for(char[] cs : chars){
			sb.append(cs);
		}
		return sb.toString().toCharArray();
	}
}
