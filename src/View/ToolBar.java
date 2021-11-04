package View;

import Model.ElementManager;
import Model.Type;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import Utils.*;
public class ToolBar extends JPanel implements ToolIf {

    private PanelIf panelIf;
    private Type chooseType=Type.None;
    private String inputValue;

    ImageIcon lineIcon,ovalIcon,inputIcon,recIcon;
    public void setPanelIf(PanelIf p){
        panelIf=p;
    }
    @Override
    public Type getButton() {
        return chooseType;
    }

    @Override
    public void setButtonNone() {
        chooseType=Type.None;
    }

    class ImgClick implements MouseListener {
        Type type;

        public ImgClick(Type type) {
            this.type = type;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            chooseType=type;
            panelIf.setSelect(false);
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    class ColorPanel extends JPanel {
        private JButton []ColorBtn;

        public ColorPanel() {
            this.setLayout(new GridLayout(4,3));
            ColorBtn=new JButton[12];
            for(int i=0;i<12;i++){
                Color nowColor=define.colors[i];
                ColorBtn[i]=new JButton();
                ColorBtn[i].setBackground(nowColor);
                ColorBtn[i].setBorderPainted(false);
                ColorBtn[i].setOpaque(true);

                ColorBtn[i].addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ElementManager.getInstance().SetColor(nowColor);
                        panelIf.repaint();
                    }
                });
                this.add(ColorBtn[i]);
            }
        }
    }


    public ToolBar() {
        Dimension size=new Dimension(define.TOOLBARWIDTH,define.HEIGHT);
        this.setPreferredSize(size);
        GridLayout tmp=new GridLayout(5,1);
        tmp.setVgap(8);
        tmp.setHgap(8);
        this.setLayout(tmp);

        try {

            BufferedImage lineImg= ImageIO.read(new File("src/Image/line.png"));
            BufferedImage recImg= ImageIO.read(new File("src/Image/rec.png"));
            BufferedImage inputImg= ImageIO.read(new File("src/Image/input.png"));
            BufferedImage ovalImg= ImageIO.read(new File("src/Image/oval.png"));

            lineIcon=new ImageIcon(lineImg);
//            img.setImage(img.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_DEFAULT));
            JButton now=new JButton(lineIcon);
            now.setOpaque(true);
            now.addMouseListener(new ImgClick(Type.Line));
            now.setBorder(BorderFactory.createRaisedBevelBorder());
            this.add(now);

            recIcon=new ImageIcon(recImg);
//            img.setImage(img.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_DEFAULT));
            now=new JButton(recIcon);
            now.setOpaque(true);
            now.addMouseListener(new ImgClick(Type.Rectangle));
            now.setBorder(BorderFactory.createRaisedBevelBorder());
            this.add(now);

            inputIcon=new ImageIcon(inputImg);
//            img.setImage(img.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_DEFAULT));
            now=new JButton(inputIcon);
            now.setOpaque(true);
            now.setBorder(BorderFactory.createRaisedBevelBorder());
            now.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String input = JOptionPane.showInputDialog(ToolBar.this,"Please input a value:","Input",JOptionPane.INFORMATION_MESSAGE);
                    if(input!=null&&!"".equals(input)){
                        panelIf.addTextElement(input);
                    }
                    panelIf.repaint();
                }
            });
            this.add(now);


            ovalIcon=new ImageIcon(ovalImg);
//            img.setImage(img.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_DEFAULT));
            now=new JButton(ovalIcon);
            now.setOpaque(true);
            now.setBorder(BorderFactory.createRaisedBevelBorder());
            now.addMouseListener(new ImgClick(Type.Oval));
            this.add(now);

        }   catch (IOException e){
            System.out.println(e.toString());
        }
        this.add(new ColorPanel());
    }

    public void initIconSize(){


        lineIcon.setImage(lineIcon.getImage().getScaledInstance(this.getWidth(),this.getHeight()/5,Image.SCALE_DEFAULT));
        recIcon.setImage(recIcon.getImage().getScaledInstance(this.getWidth(),this.getHeight()/5,Image.SCALE_DEFAULT));
        ovalIcon.setImage(ovalIcon.getImage().getScaledInstance(this.getWidth(),this.getHeight()/5,Image.SCALE_DEFAULT));
        inputIcon.setImage(inputIcon.getImage().getScaledInstance(this.getWidth(),this.getHeight()/5,Image.SCALE_DEFAULT));
        repaint();
    }
}
