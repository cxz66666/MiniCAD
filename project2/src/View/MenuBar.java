package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import Utils.*;
import static java.awt.event.KeyEvent.*;

public class MenuBar extends JMenuBar {
    private PanelIf panelIf;
    public MenuBar() {
        panelIf=null;
        this.add(createFileMenu());
    }
    public void  setPanelIf(PanelIf p){
        panelIf=p;
    }
    private JMenu createFileMenu(){
        JMenu menu=new JMenu("文件(F)");
        menu.setMnemonic(VK_F);    //设置快速访问符


        JMenuItem   item=new JMenuItem("打开(O)", VK_O);
        item.setAccelerator(KeyStroke.getKeyStroke(VK_O,ActionEvent.CTRL_MASK));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Container frame = MenuBar.this;
                do{
                    frame = frame.getParent();
                }
                while (!(frame instanceof JFrame));

                FileDialog op = new FileDialog((JFrame)frame, "Open", FileDialog.LOAD);
                op.setVisible(true);
                String path = op.getDirectory();
                String fileName = op.getFile();
                if (path == null || fileName == null){
                    return;
                }
                try {
                Utils.File.open(path,fileName);
                } catch (FileNotFoundException exception) {
                    JOptionPane.showMessageDialog(null, "Error", "File not found!", JOptionPane.ERROR_MESSAGE);
                } catch (IOException exception) {
                    JOptionPane.showMessageDialog(null, "Error", "IO exception!", JOptionPane.ERROR_MESSAGE);
                }
                panelIf.repaint();
            }
        });
        menu.add(item);


        item=new JMenuItem("保存(S)", VK_S);
        item.setAccelerator(KeyStroke.getKeyStroke(VK_S,ActionEvent.CTRL_MASK));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Container frame = MenuBar.this;
                do{
                    frame = frame.getParent();
                }
                while (!(frame instanceof JFrame));

                FileDialog save = new FileDialog((JFrame)frame, "Save", FileDialog.SAVE);
                save.setVisible(true);
                String path = save.getDirectory();
                String fileName = save.getFile();
                System.out.println(path);
                System.out.println(fileName);
                if(path==null||fileName==null){
                    return;
                }
                try {
                    Utils.File.save(path,fileName);
                } catch (IOException exception){
                    JOptionPane.showMessageDialog(null,"save error!","Io exception!",JOptionPane.ERROR_MESSAGE);
                }
                panelIf.repaint();
            }
        });
        menu.add(item);
        menu.addSeparator();

        item=new JMenuItem("退出(E)", VK_E);
        item.setAccelerator(KeyStroke.getKeyStroke(VK_E,ActionEvent.CTRL_MASK));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Container frame = MenuBar.this;
                do{
                    frame = frame.getParent();
                }
                while (!(frame instanceof JFrame));
                ((JFrame) frame).dispose();
            }
        });
        menu.add(item);

        return menu;
    }
}
