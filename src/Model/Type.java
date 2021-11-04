package Model;

import java.awt.*;

public enum Type {
    Line,Rectangle,Oval,StringText,None;
    public static Element getNewElement(Type type, Point point){
        if (type==None){
            return null;
        }
        switch (type){
            case Line:
                return new Line(point);
            case Rectangle:
                return new Rectangle(point);
            case Oval:
                return new Oval(point);
            case StringText:
                return new StringText(point,"test");
        }
        return null;
    }

    public static Element getNewElement(Type type, Point point,String inputValue){
        if (type==None){
            return null;
        }
        switch (type){
            case Line:
                return new Line(point);
            case Rectangle:
                return new Rectangle(point);
            case Oval:
                return new Oval(point);
            case StringText:
                return new StringText(point,inputValue);
        }
        return null;
    }
}
