package org.th3falc0n.nn2.packets.handler;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.th3falc0n.nn2.Address;
import org.th3falc0n.nn2.Port;
import org.th3falc0n.nn2.Router;
import org.th3falc0n.nn2.packets.Packet;

public class HandlerRouting extends PacketHandler {
	public static PacketHandler $ = new HandlerRouting();

	public static Packet getRequestHopCountForAddressPacket(Address to, Address dest) {
		Packet p = $.getEmptyPacketWithID(to);
		
		ByteBuffer buffer = ByteBuffer.allocate(17);
		
		buffer.put((byte) 0);
		buffer.put(dest.getArray());
		
		p.setData(buffer.array());
		return p;
	}

	public static Packet getAnswerHopCountForAddressPacket(Address to, Address dest, int hops) {
		Packet p = $.getEmptyPacketWithID(to);
		
		ByteBuffer buffer = ByteBuffer.allocate(21);
		
		buffer.put((byte) 1);
		buffer.put(dest.getArray());
		buffer.putInt(hops);
		
		p.setData(buffer.array());
		return p;
	}


	@Override
	public void handlePacket(Packet packet, Port port) {
		ByteBuffer buf = ByteBuffer.wrap(packet.getData());
		int id = buf.get();
		byte[] addr = new byte[16];
		buf.get(addr);
		
		Address address = new Address(addr);
		
		switch(id) {
		case 0:
			port.enqueuePacket(getAnswerHopCountForAddressPacket(packet.getSource(), address, Router.$Instance.getIdealHopsForAddress(address)));
			break;
		case 1:
			int hops = buf.getInt();
			
			if(Router.$Instance.getIdealHopsForAddress(address) > hops) {
				port.log("learned new route for " + address.toString());
				Router.$Instance.learnRoute(port, address.toString(), hops);
			}
			
			break;
		}
	}

}
