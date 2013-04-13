package org.th3falc0n.nn2.packets.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Random;

import org.th3falc0n.nn2.Address;
import org.th3falc0n.nn2.Port;
import org.th3falc0n.nn2.Router;
import org.th3falc0n.nn2.packets.Packet;

public class HandlerDistribute extends PacketHandler {
	public static PacketHandler $ = new HandlerDistribute();

	public static Packet getRequestPacket(Address to) {
		Packet p = $.getEmptyPacketWithID(to);		
		p.setData(new byte[] { 0 });
		return p;
	}
	
	public static Packet getAnnouncePacket(Address to, Address input) {
		Packet p = $.getEmptyPacketWithID(to);	

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write(1);
		
		boolean sent = false;

		while(!sent) {
			Port rnd = Router.$Instance.ports.get(new Random().nextInt(Router.$Instance.ports.size()));
			
			if(!rnd.getRemoteAddress().equals(input) || Router.$Instance.ports.size() <= 1) {
				try {
					out.write(rnd.getRemoteIP().getAddress());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sent = true;
			}
		}
		
		p.setData(out.toByteArray());
		
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return p;
	}
	
	@Override
	public void handlePacket(Packet packet, Port port) {
		ByteBuffer buf = ByteBuffer.wrap(packet.getData());
		
		int subID = buf.get();
		
		if(subID == 0) {
			Router.$Instance.routePacket(getAnnouncePacket(packet.getSource(), packet.getInput()));
		}
		
		if(subID == 1) {
			byte[] ip = new byte[4];
			
			try {
				InetAddress addr = Inet4Address.getByAddress(ip);
				
				Router.$Instance.addPort(new Port(new Socket(addr, 9192)), false);
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
