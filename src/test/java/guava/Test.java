package guava;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * descrption:
 * authohr: wangji
 * date: 2018-03-02 11:08
 */
public class Test {

    public static void main(String[] args) throws IOException {
        DatagramSocket socket=new DatagramSocket(60000);
        while(true){
            byte[] buf=new byte[1024];
            DatagramPacket packet=new DatagramPacket(buf,buf.length);
            socket.receive(packet);
//            String data=new String(packet.getData(),0,packet.getLength());
//            System.out.println(data);
            byte[] bytes=packet.getData();
            for(int i=0;i<packet.getLength();i++){
                System.out.println(Integer.toHexString(bytes[i]));
            }
        }
    }
}
