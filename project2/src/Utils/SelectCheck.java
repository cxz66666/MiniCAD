package Utils;


import Model.Element;

import java.awt.*;
import java.util.List;

public class SelectCheck {
    public static boolean test(List<Element>list, Point point){
        for(Element element:list){
            if(element.inElement(point)){
                return true;
            }
        }
        return false;
    }
}
