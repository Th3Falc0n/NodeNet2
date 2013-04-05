package org.th3falc0n.nn2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.th3falc0n.nn2.packets.handler.HandlerHandshake;
import org.th3falc0n.nn2.packets.handler.PacketHandler;

public class Router {
	public static Router $Instance = new Router();
	
	public static void main(String[] args) {
		try {
			$Instance.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	volatile List<Port> ports;
	
	volatile Address address;
	
	public Router() {
		ports = new ArrayList<Port>();
	}
	
	public void start() throws IOException {
		PacketHandler.init();
		
		ConnectionListener listener = new ConnectionListener();
		listener.startListening();
		
		BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));

		address = new Address();
		
		//TODO: No console here.
		while(true) {
			Socket sock = new Socket("localhost", Integer.parseInt(sysin.readLine()));
			
			Port port = new Port(sock);
			addPort(port, false);
		}
	}
	
	public Address getAddress() {
		return address;
	}
	
	int portID = 0;
	
	public void addPort(Port port, boolean server) {
		port.init(server, portID++);
		ports.add(port);
		
		if(!server) {
			port.queue.add(HandlerHandshake.getRequestPacket(Address.getStraightcastAddress()));
		}
	}
}
