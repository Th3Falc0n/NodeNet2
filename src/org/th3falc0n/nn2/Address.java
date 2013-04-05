package org.th3falc0n.nn2;

import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class Address {
	public static Address getStraightcastAddress() {
		return new Address(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
	}
	
	byte[] raw = new byte[16];
	
	public Address() {
		Random rnd = new Random();

		rnd.nextBytes(raw);
		
		if(raw[15] == 0) raw[15] = 1;
	}
	
	public Address(String from) {
		raw = DatatypeConverter.parseHexBinary(from);
	}
	
	public Address(byte[] addr) {
		if(addr.length == 16) {
			raw = addr;
		}
		else
		{
			throw new IllegalArgumentException("Invalid Address");
		}
	}
	
	public boolean isStraightcast() {
		return this.toString().equals(getStraightcastAddress().toString());
	}
	
	@Override
	public String toString() {
	    return DatatypeConverter.printHexBinary(raw);
	}
	
	public byte[] getArray() {
		return raw.clone();
	}
}
