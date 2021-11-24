package Model;

import java.awt.*;

public interface behave {
    //moveTo只是简单的算增量，并不是移动到的位置
    void moveTo(Point point);
    //调整当前的size
    void reSize(Point point);
    //变长
    void prolong();
    //变短
    void shorten();
    //点p是否选中该element
    boolean inElement(Point p);
}
