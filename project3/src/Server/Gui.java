package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Gui extends JFrame implements GUIIntf {
    private JTextField portText;
    private JButton portButton;

    private JScrollPane scrollPanel;

    private JTextField nickText;
    private JButton nickButton;

    private JTextField messageText;
    private JButton messageButton;

    private JTextArea serverTextArea;



    private ServerIntf serverIntf;

    public void setServerIntf(ServerIntf s) {
        this.serverIntf = s;
        serverIntf.onChangeNickName("服务器大哥");

    }
    public Gui() {
        portText = new JTextField(30);
        portButton = new JButton("开始");
        serverTextArea = new JTextArea();
        scrollPanel = new JScrollPane(serverTextArea);
        nickText = new JTextField("服务器大哥",30);
        nickButton = new JButton("确认");
        messageButton = new JButton("确认");

        messageText = new JTextField(30);

        this.setTitle("服务端");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 800);
        this.setLayout(new BorderLayout());

        //上面部分的按钮
        JPanel north = new JPanel();
        north.add(new JLabel("端口", JLabel.LEFT));
        north.add(portText);
        north.add(portButton);
        this.add(north, BorderLayout.NORTH);

        //中间部分
        serverTextArea.setFocusable(false);
        scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPanel, BorderLayout.CENTER);


        //下方部分
        JPanel south = new JPanel(), s1 = new JPanel(), s2 = new JPanel();
        south.setLayout(new  GridLayout(2,1));
        s1.add(new JLabel("我的昵称", JLabel.LEFT));
        s1.add(nickText);
        s1.add(nickButton);

        s2.add(new JLabel("消息", JLabel.LEFT));
        s2.add(messageText);
        s2.add(messageButton);

        south.add(s1);
        south.add(s2);

        this.add(south, BorderLayout.SOUTH);

        this.setVisible(true);


        //-----------------
        nickButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!nickText.getText().isEmpty()&&serverIntf!=null){
                    serverIntf.onChangeNickName(nickText.getText());
                } else if(serverIntf==null){
                    JOptionPane.showMessageDialog(Gui.this, "初始化错误");
                } else {
                    JOptionPane.showMessageDialog(Gui.this, "请输入昵称");
                }
            }
        });
        portButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!portText.getText().isEmpty()&&serverIntf!=null){
                    try{
                        int number = Integer.parseInt(portText.getText());
                        serverIntf.onStart(number);
                    }
                    catch (NumberFormatException ex){
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(Gui.this, "请输入正确的数字");
                    }
                } else if(serverIntf==null){
                    JOptionPane.showMessageDialog(Gui.this, "初始化错误");
                } else {
                    JOptionPane.showMessageDialog(Gui.this, "请输入端口号");
                }
            }
        });

        messageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!messageText.getText().isEmpty()&&serverIntf!=null){
                    serverIntf.onSendMsg(messageText.getText());
                } else if(serverIntf==null){
                    JOptionPane.showMessageDialog(Gui.this, "初始化错误");
                } else {
                    JOptionPane.showMessageDialog(Gui.this, "请输入内容");
                }
            }
        });


    }

    @Override
    public void appendMessage(String msg) {
        serverTextArea.append(msg);
        System.out.println(msg);
    }

    @Override
    public void showMessageDialog(String msg) {
        JOptionPane.showMessageDialog(Gui.this, msg);
    }
}
