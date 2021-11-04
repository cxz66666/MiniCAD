import View.MenuBar;
import View.Panel;
import View.ToolBar;

import javax.swing.*;
import Utils.*;
import java.awt.*;
import java.io.IOException;

public class Main extends JFrame {
    public Main()  {
        MenuBar menuBar=new MenuBar();
        ToolBar toolBar=new ToolBar();
        Panel panel=new Panel();
        toolBar.setPanelIf(panel);
        panel.setToolIf(toolBar);

        this.setTitle("MiniCAD");
        this.setSize(define.WIDTH,define.HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        this.add(menuBar,BorderLayout.NORTH);
        this.add(toolBar,BorderLayout.EAST);
        this.add(panel,BorderLayout.CENTER);
        this.setVisible(true);

        toolBar.initIconSize();

    }

    public static void main(String[] args) {
        new Main();
    }
}
