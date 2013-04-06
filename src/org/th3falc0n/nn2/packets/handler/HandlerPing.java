package org.th3falc0n.nn2.packets.handler;

import org.th3falc0n.nn2.Address;
import org.th3falc0n.nn2.Port;
import org.th3falc0n.nn2.packets.Packet;

public class HandlerPing extends PacketHandler {
	public static PacketHandler $ = new HandlerPing();
	
	public static Packet getPingPacket(Address to) {
		Packet p = $.getEmptyPacketWithID(to);
		p.setData(new byte[] { (byte) 142 });
		return p;
	}
	
	public static Packet getPongPacket(Address to) {
		Packet p = $.getEmptyPacketWithID(to);
		p.setData(new byte[] { (byte) 143 });
		return p;
	}

	@Override
	public void handlePacket(Packet packet, Port port) {
		if(packet.getData()[0] == (byte) 142) {
			port.log("ping received with " + packet.getHops() + " hops");
			port.enqueuePacket(getPongPacket(packet.getSource()));
		}
		
		if(packet.getData()[0] == (byte) 143) {
			port.log("pong received after " + packet.getHops() + " hops");
		}
	}


}
