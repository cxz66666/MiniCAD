package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import static java.awt.event.KeyEvent.*;

public class MenuBar extends JMenuBar {
    public MenuBar() {
        this.add(createFileMenu());
    }

    private JMenu createFileMenu(){
        JMenu menu=new JMenu("文件(F)");
        menu.setMnemonic(VK_F);    //设置快速访问符


        JMenuItem   item=new JMenuItem("打开(O)", VK_O);
        item.setAccelerator(KeyStroke.getKeyStroke(VK_O,ActionEvent.CTRL_MASK));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog op = new FileDialog((Frame) MenuBar.this.getParent(), "Open", FileDialog.LOAD);
                op.setVisible(true);
                String path = op.getDirectory();
                String fileName = op.getFile();
                if (path == null || fileName == null){
                    return;
                }
                try {
                    IOFunction.open(path, fileName);
                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(null, "Error", "File not found!", JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error", "IO exception!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        menu.add(item);


        item=new JMenuItem("保存(S)", VK_S);
        item.setAccelerator(KeyStroke.getKeyStroke(VK_S,ActionEvent.CTRL_MASK));
        menu.add(item);
        menu.addSeparator();

        item=new JMenuItem("退出(E)", VK_E);
        item.setAccelerator(KeyStroke.getKeyStroke(VK_E,ActionEvent.CTRL_MASK));
        menu.add(item);

        return menu;
    }
}
