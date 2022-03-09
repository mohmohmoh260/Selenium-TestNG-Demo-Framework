package com.test.utilities;

public class BufferHandler {
	
	public synchronized String saveBufferNumber(String bufferValue) {
	
		// remove all alphabets and spaces
		bufferValue = bufferValue.replaceAll("[^\\d.]", "");
		bufferValue = bufferValue.replaceAll("\\s","");
		
		return bufferValue;
	}
}// Close Main Class
