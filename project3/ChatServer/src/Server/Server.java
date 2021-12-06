package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.*;

public class Server implements ServerIntf {

    private GUIIntf guiIntf;
    private String serverName;
    private SendCenter sendCenter;
    private final String END="bye";
    public void setGuiIntf(GUIIntf guiIntf) {
        this.guiIntf = guiIntf;
    }

    public Server(){
    }

    @Override
    public void onStart(int port) {

        //监听线程
        new Thread(()->{
            ServerSocket serverSocket;
            try {
                serverSocket=new ServerSocket(port);
                guiIntf.appendMessage("监听:"+port+"端口，等待客户端连接\n");
                while (true){
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new Client(clientSocket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
                guiIntf.showMessageDialog("port建立失败");
            }
        }).start();

        //消息发送线程
        sendCenter=new SendCenter();
        new Thread(sendCenter).start();
    }

    @Override
    public void onChangeNickName(String newName) {
        System.out.printf("修改服务器名:[old] %s,[new] %s",serverName,newName);
        serverName=newName;
    }

    @Override
    public void onSendMsg(String msg) {
        guiIntf.appendMessage(String.format("Server %s send new message\n%s\n", serverName,msg));
        sendCenter.sendToClient(String.format("Server %s send new message\n%s\n", serverName,msg));
    }



    public class Client implements Runnable,SendMsgIntf{
        Socket socket;
        BufferedReader bufferedReader;
        PrintStream printStream;
        Boolean disconnect;
        public Client(Socket s) {
            this.socket = s;
            disconnect=false;
            try {
                bufferedReader=new BufferedReader(new InputStreamReader(s.getInputStream()));
                printStream=new PrintStream(s.getOutputStream());
                guiIntf.appendMessage(String.format("%s connect to Server!\n",getFullInfo()));
                sendCenter.addSend(this);

                printStream.println("Hello World!");
                printStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public String getFullInfo(){
            if(socket==null){
                return "连接为空";
            }
            return String.format("[remote]IP:%s,Port:%d  [server]IP:%s, Port:%d",socket.getInetAddress().toString(),socket.getPort(),socket.getLocalAddress(),socket.getLocalPort());
        }
        public String getRemoteInfo(){
            if(socket==null){
                return "连接为空";
            }
            return String.format("[remote]IP:%s,Port:%d",socket.getInetAddress().toString(),socket.getPort());
        }
        @Override
        public void run() {
            String newMessage;
            try {
                while ((newMessage=bufferedReader.readLine())!=null){
                    if(newMessage.equals(END)){
                        break;
                    }
                    guiIntf.appendMessage(String.format("New message %s\n%s\n", getFullInfo(), newMessage));
                    sendCenter.sendToClient(String.format("New message %s\n%s\n", getRemoteInfo(), newMessage));
                }
                this.disconnect=true;
                sendCenter.disConnect();
                guiIntf.appendMessage(String.format("Disconnect %s\n",getFullInfo()));
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void SendMsg(String msg) {
            printStream.println(msg);
            printStream.flush();
        }

        @Override
        public Boolean GetStatus() {
            return disconnect;
        }
    }


    interface SendMsgIntf{
        void SendMsg(String msg);
        Boolean GetStatus();
    }
    public class SendCenter implements Runnable{

        private final List<SendMsgIntf>sends;
        private final Queue<String> messageQueue;

        public SendCenter() {
           sends=new ArrayList<>();
           messageQueue=new ArrayDeque<>();
        }
        public void addSend(SendMsgIntf s){
            synchronized(this){
                sends.add(s);
            }
        }
        public void disConnect(){
            synchronized (this){
                sends.removeIf(SendMsgIntf::GetStatus);
            }
        }
        // 使用消息队列
        public void sendToClient(String msg){
            synchronized (this){
                messageQueue.add(msg);
                this.notify();
            }

        }
        @Override
        public void run() {
            while(true){
                    synchronized (this){
                        try {
                            while (!messageQueue.isEmpty()){
                                String msg=messageQueue.peek();
                                messageQueue.remove();
                                sends.stream().forEach(r->{
                                    try {
                                        r.SendMsg(msg);
                                    } catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                });
                            }
                            this.wait();
                        } catch (InterruptedException e ){
                            e.printStackTrace();
                        }
                    }
            }
        }
    }

}
