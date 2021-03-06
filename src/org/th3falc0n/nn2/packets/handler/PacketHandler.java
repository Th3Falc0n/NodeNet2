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
		HandlerRouting.			$.register("routing", 		1);
		HandlerPing.			$.register("ping", 			2);
		HandlerVersion.         $.register("version",       3);
		HandlerDistribute.      $.register("p2pdist",       4);
		HandlerStreaming.		$.register("streaming",  1337);
	}
	
	public static void handlePacketForID(Packet packet, Port port) {
		if(!idMapping.containsKey(packet.getPacketID())) { port.log("Inprocessible packet received ID=" + packet.getPacketID()); return; }
		
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
