package chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientBackground {

	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private ClientGui gui;
	private String msg;
	private String nickName;

	public final void setGui(ClientGui gui) {
		this.gui = gui;
	}

	public void connet() {
		try {
			socket = new Socket("127.0.0.1", 7777);
			System.out.println("서버 대기중...");
			
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			
			//?젒?냽?븯?옄留덉옄 ?땳?꽕?엫 ?쟾?넚?븯硫?. ?꽌踰꾧? ?씠嫄? ?땳?꽕?엫?쑝濡? ?씤?떇?쓣 ?븯怨좎꽌 留듭뿉 吏묒뼱?꽔寃좎??슂?
			out.writeUTF(nickName); 
			System.out.println("?겢?씪?씠?뼵?듃 : 硫붿떆吏? ?쟾?넚?셿猷?");
			while(in!=null){
				msg=in.readUTF();
				gui.appendMsg(msg);				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ClientBackground clientBackground = new ClientBackground();
		clientBackground.connet();
	}

	public void sendMessage(String msg2) {
		try {
			out.writeUTF(msg2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setNickname(String nickName) {
		this.nickName = nickName;
	}

}
