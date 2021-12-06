package Server;

public interface ServerIntf {
    void onStart(int port);
    void onChangeNickName(String newName);
    void onSendMsg(String msg);
}
