package org.th3falc0n.nn2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListener {
	ServerSocket ssock;
	
	public ConnectionListener() {
		int port = 9192;
		
		while(ssock == null) {
			try {
				ssock = new ServerSocket(port);
			} catch (IOException e) {
				port++;
			}
		}
		
		System.out.println("Listening on " + port);
	}
	
	public void startListening() {
		Thread lstn = new Thread() {
			@Override
			public void run() {
				super.run();
				
				while(true) {
					Socket sock = null;
					
					try {
						sock = ssock.accept();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(sock != null) {
						Port port = new Port(sock);
						Router.$Instance.addPort(port, true);
					}
				}
			}
		};
		
		lstn.start();
	}
}
