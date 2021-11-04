package Model;

import java.awt.*;

public interface behave {
    //moveTo只是简单的算增量，并不是移动到的位置
    void moveTo(Point point);
    void reSize(Point point);
    void prolong();
    void shorten();

    boolean inElement(Point p);
}
