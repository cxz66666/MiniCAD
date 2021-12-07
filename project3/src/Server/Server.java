package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.*;
import java.util.concurrent.*;

public class Server implements ServerIntf {
    //与gui交互的接口
    private GUIIntf guiIntf;
    private String serverName;
    private SendCenter sendCenter;
    //客户端输入的结束字符串
    private final String END="bye";
    //最大的线程数，如果超过了该线程 则连接会等待
    private final int MaxThread=2000;
    //线程池
    private ExecutorService threadPool;
    public void setGuiIntf(GUIIntf guiIntf) {
        this.guiIntf = guiIntf;
    }

    public Server(){
        sendCenter=new SendCenter();
        threadPool= new ThreadPoolExecutor(0,MaxThread,60L, TimeUnit.SECONDS,
                                                  new SynchronousQueue<Runnable>());

    }

    @Override
    public void onStart(int port) {
        threadPool.submit(()->{
            //监听线程
            ServerSocket serverSocket;
            try {
                serverSocket=new ServerSocket(port);
                guiIntf.appendMessage("监听:"+port+"端口，等待客户端连接\n");
                while (true){
                    Socket clientSocket = serverSocket.accept();
                    threadPool.submit(new Client(clientSocket));
                }
            } catch (IOException e) {
                e.printStackTrace();
                guiIntf.showMessageDialog("port建立失败");
            }
        });
        threadPool.submit(sendCenter);

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


//客户端类，实现了SendMsgIntf接口用来向客户端发送数据
    public class Client implements Runnable,SendMsgIntf{
        //原始的socket
        Socket socket;
        //接收
        BufferedReader bufferedReader;
        //发送
        PrintStream printStream;
        //是否断开
        Boolean disconnect;
        //初始化各种变量
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
            //使用bufferReader读取数据
            String newMessage;
            try {
                while ((newMessage=bufferedReader.readLine())!=null){
                    if(newMessage.equals(END)){
                        break;
                    }
                    guiIntf.appendMessage(String.format("New message %s\n%s\n", getFullInfo(), newMessage));
                    sendCenter.sendToClient(String.format("New message %s\n%s\n", getRemoteInfo(), newMessage));
                }
                //连接断开，客户端主动
                this.disconnect=true;
                sendCenter.disConnect();
                guiIntf.appendMessage(String.format("Disconnect %s\n",getFullInfo()));
                //关闭连接
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void sendMsg(String msg) {
            printStream.println(msg);
            printStream.flush();
        }

        @Override
        public Boolean getStatus() {
            return disconnect;
        }
    }


    interface SendMsgIntf{
        //向客户端发送数据
        void sendMsg(String msg);
        //获取当前连接状态
        Boolean getStatus();
    }
    //
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
                sends.removeIf(SendMsgIntf::getStatus);
            }
        }
        // 使用消息队列,将 一条信息加入队列中，注意需要加锁，之后唤醒正在等待的发送线程。
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
                            //还有没有发的数据
                            while (!messageQueue.isEmpty()){
                                String msg=messageQueue.peek();
                                messageQueue.remove();
                                sends.forEach(r->{
                                    try {
                                        r.sendMsg(msg);
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
