package org.th3falc0n.nn2.packets.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.th3falc0n.nn2.Address;
import org.th3falc0n.nn2.Config;
import org.th3falc0n.nn2.Port;
import org.th3falc0n.nn2.packets.Packet;

public class HandlerDistribute extends PacketHandler {
	public static PacketHandler $ = new HandlerDistribute();

	public static Packet getRequestPacket(Address to) {
		Packet p = $.getEmptyPacketWithID(to);		
		p.setData(new byte[] { 0 });
		return p;
	}
	
	public static Packet getAnnouncePacket(Address to, Address addr) {
		Packet p = $.getEmptyPacketWithID(to);	

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write(1);
		
		//TODO: You really wanna give it a **** NodeNet address? YOU NEED THAT IP. YOU WANT A NEW PORT! IDIOT!!!
		
		try {
			out.write(addr.getArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return p;
	}
	
	@Override
	public void handlePacket(Packet packet, Port port) {
		
	}

}
