package org.th3falc0n.nn2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.th3falc0n.nn2.packets.Packet;
import org.th3falc0n.nn2.packets.handler.PacketHandler;

public class Port {
	Socket sock;
	int portID = 0;
	
	volatile boolean validConnection = false;
	
	volatile DataOutputStream out;
	volatile DataInputStream in;
	
	volatile Queue<Packet> queue = new ConcurrentLinkedQueue<Packet>();
	
	volatile Address remoteAddress;

	public class SendThread extends Thread {
		@Override
		public void run() {
			while(true) {
				try {
					if(queue.size() > 0) {
						queue.poll().sendOnStream(out);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public class RecvThread extends Thread {
		@Override
		public void run() {
			boolean stop = false;
			while(!stop) {
				try {
					Packet income = Packet.pullFromStream(in);
					if(income.getDestination().toString().equals(Router.$Instance.getAddress().toString())
					|| income.getDestination().isStraightcast()) {
						PacketHandler.handlePacketForID(income, Port.this);
					}
					else
					{
						//TODO: ROUTING!!!
						Port.this.log("Packet for routing received. This version has no routing :( Packet is lost forever");
					}
				} catch (IOException e) {
					e.printStackTrace();
					stop = true;
				}
			}
		}
	}
	
	public Port(Socket sock) {
		this.sock = sock;
	}
	
	public boolean isValidated() {
		return validConnection;
	}
	
	public void setValidated() {
		validConnection = true;
	}
	
	public void setRemoteAddress(Address remote) {
		remoteAddress = remote;
	}
	
	public Address getRemoteAddress() {
		return remoteAddress;
	}
	
	public void enqueuePacket(Packet packet) {
		queue.add(packet);
	}
	
	public void init(boolean server, int portID) {
		try {
			this.portID = portID;
			
			out = new DataOutputStream(sock.getOutputStream());
			in = new DataInputStream(sock.getInputStream());
			
			new SendThread().start();
			new RecvThread().start();
			
			log(sock.getRemoteSocketAddress().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void log(String msg) {
		if(remoteAddress == null) {
			System.out.println(portID + ": " + msg);
		}
		else
		{
			System.out.println(portID + "[" + remoteAddress.toString() + "]: " + msg);
		}
	}
}
