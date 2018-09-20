package shuchaowen.core.util;

import java.util.Arrays;

public class ByteArray {
	private byte[] array;
	
	private int size = 0;
	private int readIndex = 0;
	
	public ByteArray(){
		array = new byte[1024];
	}
	
	public ByteArray(int initCapacity){
		array = new byte[initCapacity];
	}
	
	public void add(byte ...bytes){
		int newSize = size + bytes.length;
		checkCapacity(newSize);
		for(byte b : bytes){
			array[size] = b;
			size ++;
		}
	}
	
	public void add(byte[] bytes, int begin, int offset){
		if(offset < 0 || begin < 0){
			throw new NullPointerException();
		}
		int newSize = size + offset;
		checkCapacity(newSize);
		for(int i=begin; i<(begin + offset); i++){
			array[size] = bytes[i];
			size ++;
		}
	}
	
	private void checkCapacity(int newSize){
		if(newSize > array.length){
			array = Arrays.copyOf(array, newSize);
		}
	}
	
	public int size(){
		return size;
	}
	
	public byte[] asBytes(){
		return Arrays.copyOfRange(array, 0, size);
	}
	
	public void resetSize(){
		size = 0;
	}
	
	public void resetReadIndex(){
		setReadIndex(0);
	}
	
/*	public byte[] nextBytes(){
		
	}*/
	
	public int nextInt(){
		setReadIndex(getReadIndex() + 4);
		return readInt(0);
	}
	
	private int readInt(int off) {
        return ((array[off + 3] & 0xFF)      ) +
                ((array[off + 2] & 0xFF) <<  8) +
                ((array[off + 1] & 0xFF) << 16) +
                ((array[off    ]       ) << 24);
    }

	public int getReadIndex() {
		return readIndex;
	}

	public void setReadIndex(int readIndex) {
		this.readIndex = readIndex;
	}
}
