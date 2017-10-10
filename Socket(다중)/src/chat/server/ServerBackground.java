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
			Collections.synchronizedMap(clientsMap); // ?΄κ±? κ΅ν΅? λ¦? ?΄μ€λ?€^^
			serverSocket = new ServerSocket(7777);
			while (true) {
			
				System.out.println("?λ²? ??κΈ°μ€...");
				socket = serverSocket.accept(); // λ¨Όμ? ?λ²κ? ? ?Ό?? κ³μ λ°λ³΅?΄? ?¬?©?λ₯? λ°λ?€.
				System.out.println(socket.getInetAddress() + "?? ? ???΅??€.");
				// ?¬κΈ°μ ?λ‘μ΄ ?¬?©? ?°? ? ?΄??€ ??±?΄? ?μΌμ λ³΄λ?? ?£?΄μ€μΌκ² μ£ ?!
				Receiver receiver = new Receiver(socket);
				receiver.start();
			}
	}

	public static void main(String[] args) throws IOException {
		ServerBackground serverBackground = new ServerBackground();
		serverBackground.setting();
	}

	// λ§΅μ?΄?©(?΄?Ό?΄?Έ?Έ) ???₯κ³? ?­? 
	public void addClient(String nick, DataOutputStream out) throws IOException {
		sendMessage(nick + "??΄ ? ???¨?΅??€.");
		clientsMap.put(nick, out);
	}

	public void removeClient(String nick) {
		sendMessage(nick + "??΄ ?κ°??¨?΅??€.");
		clientsMap.remove(nick);
	}

	// λ©μμ§? ?΄?© ? ?
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

		/** XXX 2. λ¦¬μλ²κ? ??Ό?? ?κΈ? ?Ό?? ?€?Έ??¬ μ²λ¦¬ κ³μ..?£κΈ?.. μ²λ¦¬?΄μ£Όλ κ²?. */
		public Receiver(Socket socket) throws IOException {
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			nick = in.readUTF();
			addClient(nick, out);
		}

		public void run() {
			try {// κ³μ ?£κΈ°λ§!!
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
