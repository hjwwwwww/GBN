package udp;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import lsy.SR;
public class Receiver extends Thread {
    private static DatagramPacket receivePacket;
    private static DatagramPacket SendACK;
    private static DatagramSocket Client;
    private static byte[] receive = new byte[1024];
    private static byte[] send = new byte[20];
    private static int last;
    private static int Segments;//�����ͱ��Ķ�
    public Receiver(){
        last = -1;
        Segments = SR.SEGMENTS;
        try {
            Client = new DatagramSocket(SR.Port);
        }catch (SocketException e){
        		System.out.println("���շ�Socket�޷��򿪣�");
        }
    }
    @Override
    public void run(){
        while (true){
            receivePacket = new DatagramPacket(receive,receive.length);
            try{
                Client.receive(receivePacket);
            }catch (IOException e){
            		System.out.println("����ʧ�ܣ�");
            }
            int Sequences = -1;
            if(receive[1] == 'k'){
                Sequences = receive[0] - '0';
            }
            else {
                Sequences = (receive[0]-'0')*10+(receive[1]-'0');
            }
            //ͨ���������ָ����������
            if(Math.random()<0.8){
                if (Sequences == last+1){
                    //���������ȷ������ACK���ģ������ظ���������
                    if(Sequences / 10 == 0){
                        send = new String("ACK"+Sequences+"m").getBytes();
                    }
                    else if(Sequences / 100 == 0){
                        send = new String("ACK"+Sequences).getBytes();
                    }
                    InetAddress inetAddress = receivePacket.getAddress();
                    int clientPort = receivePacket.getPort();
                    SendACK = new DatagramPacket(send,send.length,inetAddress,clientPort);
                    try{
                        Client.send(SendACK);
                        Segments--;
                        last++;
                    }catch (IOException e){
                    }
                }
                else if(Sequences >(last+1)){
                	 System.out.println("�ͻ��˵�ǰӦ�ý��ܵ����к�"+(last+1)+"�����ݰ�,ʵ�ʽ��յ������к�Ϊ"+Sequences+"�����ݰ�");
                    if((last)/10== 0){
                     send = new String("ACK"+(last)+"m").getBytes();
                   }else if((last)/100 == 0){
                        send = new String("ACK"+(last)).getBytes();
                   }
                    InetAddress inetAddress = receivePacket.getAddress();
                    int clientPort = receivePacket.getPort();
                    SendACK = new DatagramPacket(send,send.length,inetAddress,clientPort);
                    try{
                        Client.send(SendACK);
                    }catch (IOException e){
                    }
                }

            }else{
            	//����
            }
        }
    }
}
