package Model;

import java.awt.*;
import java.io.Serializable;

import Utils.define;

public abstract class Element implements behave, Serializable {
    private float strokeWidth=2.0f;
    private Color color=Color.black;
    protected boolean select=false;

    public void wider(){
        strokeWidth++;
    }

    public void thinner(){
        if(strokeWidth>1.0f){
            strokeWidth--;
        }
    }
    public void setColor(Color c) {
        this.color=c;
    }

    public void setSelect(boolean status){
        this.select=status;
    }

    public void draw(Graphics2D g){
        g.setColor(color);
        if(select){
            g.setStroke(new BasicStroke(strokeWidth+define.selectedStroke));
        } else {
            g.setStroke(new BasicStroke(strokeWidth));
        }
    }

}

class Line extends Element {

    private double x1,y1,x2,y2;

    public Line(Point p) {
        this.x1 = this.x2=p.getX();
        this.y1 = this.y2=p.getY();
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        g.drawLine((int)x1,(int)y1,(int)x2,(int)y2);
    }

    @Override
    public void moveTo(Point point) {
        x1+=point.getX();
        x2+=point.getX();
        y1+=point.getY();
        y2+=point.getY();
    }

    @Override
    public void reSize(Point point) {
        x2=point.getX();
        y2=point.getY();
    }

    @Override
    public void prolong() {
        if(x1==x2){
            if (y1 < y2){
                y2++;
                y1--;
            } else{
                y1++;
                y2--;
            }

        } else if(x1<x2){
            double addY=(y2 - y1) / (x2 - x1);
            y2+=addY;
            y1-=addY;
            x2+=1;
            x1-=1;
        } else {
            double addY=(y2 - y1) / (x2 - x1);
            y2-=addY;
            y1+=addY;
            x2-=1;
            x1+=1;
        }
    }

    @Override
    public void shorten() {
        if (Math.abs( (x2 - x1)) >= define.minLength && Math.abs((y2 - y1)) >= define.minHeight) {
            if (x1 == x2) {
                if (y1 < y2) {
                    y2--;
                    y1++;
                } else {
                    y1--;
                    y2++;
                }

            } else if (x1 < x2) {
                double addY = (y2 - y1) / (x2 - x1);
                y2 -= addY;
                y1 += addY;
                x2 -= 1;
                x1 += 1;
            } else {
                double addY = (y2 - y1) / (x2 - x1);
                y2 += addY;
                y1 -= addY;
                x2 += 1;
                x1 -= 1;
            }
        }
    }

    @Override
    public boolean inElement(Point p) {
        int dist=-1;
        if(x1==x2){
            if((p.y<=y1&&p.y<=y2)||(p.y<=y1&&p.y<=y2)){
                dist=Math.min((int)(Math.pow(p.x-x1,2)+Math.pow(p.y-y1,2)),(int)(Math.pow(p.x-x1,2)+Math.pow(p.y-y1,2)));
            } else {
                dist=Math.abs(p.x-(int)x1);
            }
        } else {
            if((p.x<=x1&&p.x<=x2)||(p.x>=x1&&p.x>=x2)){
                dist=Math.min((int)(Math.pow(p.x-x1,2)+Math.pow(p.y-y1,2)),(int)(Math.pow(p.x-x1,2)+Math.pow(p.y-y1,2)));
            } else {
                double k = 1.0f * (y1 - y2) / (x1 - x2);
                double b = 1.0f * y1 - k * x1;
                dist=Math.abs((int)(k*p.x+b-p.y));
            }
        }

        if(dist<=define.LineDelta){
            select=true;
            return true;
        }
        return false;
    }

}


class Rectangle extends Element {
    private double x,y,width,height;
    private double orgX,orgY;
    public Rectangle(Point p) {
        this.x=this.orgX = p.getX();
        this.y =this.orgY= p.getY();

        width=height=0;
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        g.drawRect((int) x,(int) y,(int) width,(int) height);
    }

    @Override
    public void moveTo(Point point) {
        x+=point.getX();
        orgX+=point.getX();
        y+=point.getY();
        orgY+= point.getY();
    }

    @Override
    public void reSize(Point point) {
        double nextX=point.getX();
        double nextY=point.getY();
        width=Math.abs(nextX-orgX);
        height=Math.abs(nextY-orgY);
        x=Math.min(nextX,orgX);
        y=Math.min(nextY,orgY);
    }

    @Override
    public void prolong() {

        width+=width/height;
        height+=1;
    }

    @Override
    public void shorten() {
        height-=1;
        width-=width/height;

        if(width<define.minLength){
            width=define.minLength;
        }
        if(height<define.minHeight){
            height=define.minHeight;
        }
    }

    @Override
    public boolean inElement(Point p) {
        if(p.x>=x&&p.x<=x+width&p.y>=y&&p.y<=y+height){
            select=true;
            return true ;
        }
        return false;
    }
}


class Oval extends Element {
    private double x,y,width,height;
    private double orgX,orgY;

    public Oval(Point p) {
        this.x=orgX = p.getX();
        this.y=orgY = p.getY();
        width=height=0;
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        g.drawOval((int) x,(int) y,(int) width,(int) height);
    }

    @Override
    public void moveTo(Point point) {
        x+=point.getX();
        orgX+=point.getX();
        y+=point.getY();
        orgY+= point.getY();
    }

    @Override
    public void reSize(Point point) {
        double nextX=point.getX();
        double nextY=point.getY();
        width=Math.abs(nextX-orgX);
        height=Math.abs(nextY-orgY);
        x=Math.min(nextX,orgX);
        y=Math.min(nextY,orgY);
    }

    @Override
    public void prolong() {
        width+=width/height;
        height+=1;
    }

    @Override
    public void shorten() {
        width-=width/height;
        height-=1;

        if(width<define.minLength){
            width=define.minLength;
        }
        if(height<define.minHeight){
            height=define.minHeight;
        }
    }

    @Override
    public boolean inElement(Point p) {
        double a= p.x-(x+width/2);
        double  b=p.y-(y+height/2);
        double tmp=Math.pow(a,2)/Math.pow(width,2)+Math.pow(b,2)/Math.pow(height,2);
        if(tmp<0.25){
            select=true;
            return true;
        }
        return false;
    }
}


class  StringText extends  Element {

    private double x,y;
    private   String content;
    private FontMetrics fm = null;
    private Font font;
    public StringText(Point p, String content) {
        this.x = p.getX();
        this.y = p.getY();
        this.content = content;
        font=new Font("SansSarif",Font.BOLD,18);
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        fm=g.getFontMetrics(font);
       // System.out.println(fm.getHeight());
        if (select){
           g.setFont( new Font("SansSarif", Font.BOLD,font.getSize()+define.selectedString));
        } else {
            g.setFont(font);
        }
        g.drawString(content,(float) x,(float) y);
    }

    @Override
    public void moveTo(Point point) {
        this.x+=point.getX();
        this.y+=point.getY();
    }

    @Override
    public void reSize(Point point) {
        System.out.println("why we need this.");
    }

    @Override
    public void prolong() {
        font = new Font("SansSarif", Font.BOLD, font.getSize() + 1);
    }

    @Override
    public void shorten() {
        font = new Font("SansSarif", Font.BOLD, font.getSize()>define.minFontSize?font.getSize()-1:define.minFontSize);
    }
    @Override
    public void wider(){
        prolong();
    }

    @Override
    public void thinner(){
        shorten();
    }
    @Override
    public boolean inElement(Point p) {
        //System.out.printf("%d %f %f\n",p.y,y,y+fm.getHeight());
        if(p.x>=x&&p.x<=x+fm.stringWidth(content)&&p.y>=y-fm.getHeight()&&p.y<=y){
            select=true;
            return true;
        }
        return false;
    }
}