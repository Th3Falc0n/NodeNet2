package org.th3falc0n.nn2.packets.handler;

import java.util.HashMap;
import java.util.Map;

import org.th3falc0n.nn2.Address;
import org.th3falc0n.nn2.Port;
import org.th3falc0n.nn2.Router;
import org.th3falc0n.nn2.packets.Packet;

public abstract class PacketHandler {	
	public static Map<Integer, PacketHandler> idMapping = new HashMap<Integer, PacketHandler>();
	public static Map<String, PacketHandler> nameMapping = new HashMap<String, PacketHandler>();

	public static void init() {
		HandlerHandshake.		$.register("handshake",		0);
	}
	
	public static void handlePacketForID(Packet packet, Port port) {
		if(!idMapping.containsKey(packet.getPacketID())) port.log("Inprocessible packet received.");
		idMapping.get(packet.getPacketID()).handlePacket(packet, port);
	}
	
	public Packet getEmptyPacketWithID(Address to) {
		return new Packet(getPacketID(), to, Router.$Instance.getAddress());
	}
	
	public int getPacketID() {
		return id;
	}
	
	protected int id;
	
	protected void register(String name, int id) {
		this.id = id;
		idMapping.put(id, this);
		nameMapping.put(name, this);
	}
	
	public abstract void handlePacket(Packet packet, Port port);
}
