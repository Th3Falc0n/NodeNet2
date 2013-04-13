package org.th3falc0n.nn2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
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
	volatile List<String> optimizedAddresses = new Vector<String>(0, 1);
	
	volatile Address remoteAddress;
	volatile String  remoteVersion = "unknown";

	public class SendThread extends Thread {
		@Override
		public void run() {
			while(true) {
				try {
					if(queue.size() > 0) {
						queue.poll().sendOnStream(out);
					}
					else
					{
						Thread.sleep(1);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
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
					income.setInput(Port.this.getRemoteAddress());
					income.increaseHops();
					
					Router.$Instance.learnRoute(Port.this, income.getSource().toString(), income.getHops());
					
					if(income.getDestination().toString().equals(Router.$Instance.getAddress().toString())
					|| income.getDestination().isStraightcast()) {
						
						PacketHandler.handlePacketForID(income, Port.this);
					}
					else
					{						
						if(income.getHops() > 1000) {
							continue;
						}
						Port.this.log("Routing packet from " + income.getSource().toString() + " to " + income.getDestination().toString());
						Router.$Instance.routePacket(income);
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
	
	public boolean isAddressOptimized(Address addr) {
		return optimizedAddresses.contains(addr.toString());
	}
	
	public void setAddressOptimized(Address addr) {
		optimizedAddresses.add(addr.toString());
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

	public void setRemoteVersion(String version) {
		remoteVersion = version;
	}
	
	public String getRemoteVersion() {
		return remoteVersion;
	}

	public InetAddress getRemoteIP() {
		return sock.getInetAddress();
	}
}
