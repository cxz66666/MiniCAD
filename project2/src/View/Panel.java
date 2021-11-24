package View;

import Model.Element;
import Model.ElementManager;
import Model.Type;
import Utils.define;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class Panel extends JPanel implements PanelIf {
    private ToolIf toolIf;
    private boolean select=false;

    private Point start,now;

    private Type type;

    private Element element;
    public void setToolIf(ToolIf t){
        this.toolIf=t;
    }

    public Panel() {
        this.setBackground(Color.white);
        this.setFocusable(true);
        start=new Point();
        now = new Point();

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                start.x=now.x=e.getX();
                start.y=now.y=e.getY();
                //TODO 确定select的element以及是否select
                element=ElementManager.getInstance().CheckSelect(start);
                if (element!=null){
                    select=true;
                } else {
                    select=false;
                }

                repaint();
                requestFocus();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                start.x=now.x=e.getX();
                start.y=now.y=e.getY();
                type=toolIf.getButton();
                if(type!=Type.None){
                    element=Type.getNewElement(type,start);
                    ElementManager.getInstance().Add(element);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(!select){
                    if(type!=Type.None&&(Math.abs(e.getX()-start.x)<=define.existLength||Math.abs(e.getY()-start.y)<=define.existHeight)){
                        ElementManager.getInstance().Remove(element);
                    }
                        element=null;
                }
                toolIf.setButtonNone();
                //如果目前选中了元素，则不清空。否则清空
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                //如果select选中了element，此时的element为需要更改的元素
                if(select&&element!=null){
                    Point delta=new Point(e.getX()-now.x,e.getY()-now.y );
                    element.moveTo(delta);
                    now.x=e.getX();
                    now.y=e.getY();
                    repaint();
                    //如果此时正在添加新元素
                } else if(type!=Type.None&&element!=null) {
                    now.x=e.getX();
                    now.y=e.getY();
                    element.reSize(now);
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(select){
                    switch (e.getKeyChar()){
                        case '+': case '=':
                            ElementManager.getInstance().prolong();
                            break;

                        case '-': case '_':
                            ElementManager.getInstance().shorten();
                            break;

                        case '>': case '.':
                            ElementManager.getInstance().wider();
                            break;

                        case '<': case ',':
                            ElementManager.getInstance().thinner();
                            break;

                        case 'r': case 'R':
                            ElementManager.getInstance().delete();
                            select=false;
                            break;

                        default:
                            break;
                    }
                    repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        List<Element>ls=ElementManager.getInstance().getList();
        for( Element e:ls){
            e.draw((Graphics2D) g);
        }
    }

    @Override
    public void addTextElement(String inputValue) {
        Element element=Type.getNewElement(Type.StringText,new Point(define.WIDTH/2,define.HEIGHT/2),inputValue);
        ElementManager.getInstance().Add(element);
        repaint();
    }

    @Override
    public void setSelect(boolean s) {
        select=s;
        ElementManager.getInstance().resetSelect();

    }
}
