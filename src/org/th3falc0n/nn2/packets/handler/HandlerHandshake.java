package org.th3falc0n.nn2.packets.handler;

import org.th3falc0n.nn2.Address;
import org.th3falc0n.nn2.Port;
import org.th3falc0n.nn2.Router;
import org.th3falc0n.nn2.packets.Packet;

public class HandlerHandshake extends PacketHandler {
	public static PacketHandler $ = new HandlerHandshake();
	
	public static Packet getRequestPacket(Address to) {
		Packet p = $.getEmptyPacketWithID(to);
		p.setData(new byte[] { 42 });
		return p;
	}
	
	public static Packet getAcceptPacket(Address to) {
		Packet p = $.getEmptyPacketWithID(to);
		p.setData(new byte[] { 84 });
		return p;
	}
	
	@Override
	public void handlePacket(Packet packet, Port port) {		
		if(!port.isValidated()) {
			if(packet.getData()[0] == 42) {
				port.setRemoteAddress(packet.getSource());
				Router.$Instance.getRoutes().get(port).put(port.getRemoteAddress().toString(), 0);
				port.setValidated();
				port.enqueuePacket(getAcceptPacket(port.getRemoteAddress()));
			}
			else
			{
				if(packet.getData()[0] == 84) {
					port.setRemoteAddress(packet.getSource());
					Router.$Instance.getRoutes().get(port).put(port.getRemoteAddress().toString(), 0);
					port.setValidated();
				}
				else
				{
					throw new IllegalStateException("Incorrect handshake");
				}
			}
		}
		else
		{
			throw new IllegalStateException("Double handshake");
		}
	}
}
