package org.th3falc0n.nn2.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.th3falc0n.nn2.Address;

public class Packet {	
	int id;
	int hops;
	byte[] data;
	Address to;
	Address from;

	public Packet(int i, Address t, Address f) {
		id = i;
		to = t;
		from = f;
	}
	
	public void sendOnStream(DataOutputStream stream) throws IOException {
		byte[] data = getData();
		
		int length = data != null ? data.length : 0;
		
		stream.writeInt(id);
		stream.writeInt(hops);
		stream.write(to.getArray());
		stream.write(from.getArray());
		stream.writeInt(length);
		
		if(data != null) {
			stream.write(data);
		}
		
		stream.flush();
	}
	
	public static Packet pullFromStream(DataInputStream stream) throws IOException {		
		int id = stream.readInt();
		byte[] to = new byte[16];
		byte[] from = new byte[16];
		
		int hops = stream.readInt();
		
		stream.read(to);
		stream.read(from);
		
		Packet packet = new Packet(id, new Address(to), new Address(from));
		packet.hops = hops;
		
		int length = stream.readInt();
		
		byte[] buffer = new byte[length];
		
		stream.read(buffer);
		
		packet.setData(buffer);
		
		return packet;
	}
	
	public int getPacketID() {
		return id;
	}
	
	public Address getDestination() {
		return to;
	}
	
	public Address getSource() {
		return from;
	}

	public byte[] getData() {
		return data;
	}	
	
	public void setData(byte[] d) {
		data = d;
	}

	public void increaseHops() {
		hops++;
	}

	public int getHops() {
		return hops;
	}
}
