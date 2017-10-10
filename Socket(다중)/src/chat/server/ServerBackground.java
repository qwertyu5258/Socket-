package chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServerBackground {

	private ServerSocket serverSocket;
	private Socket socket;
	private ServerGui gui;
	private String msg;

	
	private Map<String, DataOutputStream> clientsMap = new HashMap<String, DataOutputStream>();

	public final void setGui(ServerGui gui) {
		this.gui = gui;
	}

	public void setting() throws IOException {
			Collections.synchronizedMap(clientsMap); // ?���? 교통?���? ?��줍니?��^^
			serverSocket = new ServerSocket(7777);
			while (true) {
			
				System.out.println("?���? ??기중...");
				socket = serverSocket.accept(); // 먼�? ?��버�? ?��?��?? 계속 반복?��?�� ?��?��?���? 받는?��.
				System.out.println(socket.getInetAddress() + "?��?�� ?��?��?��?��?��?��.");
				// ?��기서 ?��로운 ?��?��?�� ?��?��?�� ?��?��?�� ?��?��?��?�� ?��켓정보�?? ?��?��줘야겠죠?!
				Receiver receiver = new Receiver(socket);
				receiver.start();
			}
	}

	public static void main(String[] args) throws IOException {
		ServerBackground serverBackground = new ServerBackground();
		serverBackground.setting();
	}

	// 맵의?��?��(?��?��?��?��?��) ???���? ?��?��
	public void addClient(String nick, DataOutputStream out) throws IOException {
		sendMessage(nick + "?��?�� ?��?��?��?��?��?��?��.");
		clientsMap.put(nick, out);
	}

	public void removeClient(String nick) {
		sendMessage(nick + "?��?�� ?���??��?��?��?��.");
		clientsMap.remove(nick);
	}

	// 메시�? ?��?�� ?��?��
	public void sendMessage(String msg) {
		Iterator<String> it = clientsMap.keySet().iterator();
		String key = "";
		while (it.hasNext()) {
			key = it.next();
			try {
				clientsMap.get(key).writeUTF(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// -----------------------------------------------------------------------------
	class Receiver extends Thread {
		private DataInputStream in;
		private DataOutputStream out;
		private String nick;

		/** XXX 2. 리시버�? ?��?��?? ?���? ?��?��?�� ?��?��?��?�� 처리 계속..?���?.. 처리?��주는 �?. */
		public Receiver(Socket socket) throws IOException {
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			nick = in.readUTF();
			addClient(nick, out);
		}

		public void run() {
			try {// 계속 ?��기만!!
				while (in != null) {
					msg = in.readUTF();
					sendMessage(msg);
					gui.appendMsg(msg);
				}
			} catch (IOException e) {
				removeClient(nick);
			}
		}
	}
}
