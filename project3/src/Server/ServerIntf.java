package Server;

public interface ServerIntf {
    //开始监听
    void onStart(int port);
    //修改nickname
    void onChangeNickName(String newName);
    //发送信息
    void onSendMsg(String msg);
}
