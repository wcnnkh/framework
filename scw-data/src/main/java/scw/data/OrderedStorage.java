package scw.data;

public interface OrderedStorage extends Storage{
	String getFirstKey();
	
	String getLastKey();
}
