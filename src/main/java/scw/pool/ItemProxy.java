package scw.pool;

public interface ItemProxy extends Item {
	
	int getPoolIndex();

	void setPoolIndex(int index);
	
	Item getTarget();
	
	long getLastTime();
}
