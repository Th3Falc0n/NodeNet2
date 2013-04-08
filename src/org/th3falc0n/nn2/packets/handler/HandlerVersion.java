package org.th3falc0n.nn2.packets.handler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.th3falc0n.nn2.Address;
import org.th3falc0n.nn2.Config;
import org.th3falc0n.nn2.Port;
import org.th3falc0n.nn2.Router;
import org.th3falc0n.nn2.packets.Packet;

public class HandlerVersion extends PacketHandler {
	public static PacketHandler $ = new HandlerVersion();
	

	public static Packet getRequestPacket(Address to) {
		Packet p = $.getEmptyPacketWithID(to);		
		p.setData(new byte[] { 0 });
		return p;
	}
	
	public static Packet getAnswerPacket(Address to) {
		Packet p = $.getEmptyPacketWithID(to);	

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write(1);
		
		try {
			out.write(Config._VERSION.getBytes("UTF-8"));
			out.write("\n".getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		ByteArrayInputStream stream = new ByteArrayInputStream(packet.getData());
		
		switch (stream.read()) {
		case 0:
			Router.$Instance.routePacket(getAnswerPacket(packet.getSource()));
			break;
		case 1:
			try {
				port.setRemoteVersion(new BufferedReader(new InputStreamReader(stream, "UTF-8")).readLine());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		}
	}
}
