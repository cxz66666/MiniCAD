package Model;

import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ChenXuzheng
 */
public class ElementManager {
    private static ElementManager instance;
    private ElementManager(){

    }
    //选择单例中select=true的element
    private Element getSelected(){
        for(Element e:list){
            if(e.select){
                return e;
            }
        }
        return null;
    }
    public static ElementManager getInstance(){
        if (instance==null) {
            instance=new ElementManager();
        }
        return instance;
    }

    public List<Element> list=new ArrayList<Element>();

    public List<Element>getList() {
        return this.list;
    }

    public void Add(Element element){
        list.add(element);
    }
    public void Remove(Element element){
        list.remove(element);
    }

    public void ClearAll(){
        list.removeAll(list);
    }

    //将所有状态设置为false
    public void resetSelect(){
        for(Element e:list){
            e.select=false;
        }
    }
    // 如果该point落在某element范围内，则改element被设置为选中状态，返回改element
    // 否则所有element的select都置为false，同时返回null
    public Element CheckSelect(Point point){
        this.resetSelect();
        boolean ans=Utils.SelectCheck.test(list,point);
        if(!ans){
            return null;
        }
       return getSelected();
    }

    public void SetColor(Color color){
        Element element= getSelected();
        if(element!=null){
            element.setColor(color);
        }
    }

    public void prolong(){
        Element element= getSelected();
        if(element!=null){
            element.prolong();
        }
    }
    public void shorten(){
        Element element= getSelected();
        if(element!=null){
            element.shorten();
        }
    }
    public void wider(){
        Element element= getSelected();
        if(element!=null){
            element.wider();
        }
    }
    public void thinner(){
        Element element= getSelected();
        if(element!=null){
            element.thinner();
        }
    }
    public void delete(){
        Element element= getSelected();
        if(element!=null){
            this.Remove(element);
        }
    }

    public void write(ObjectOutputStream out) throws IOException {
        if(out==null){
            return ;
        }
        for(Element e:list){
            out.writeObject(e);
        }
    }
    public void read(ObjectInputStream in) throws IOException {
        if(in ==null){
            return;
        }
        this.ClearAll();
        try {
            while (true){
                this.Add((Element) in.readObject());
            }
        } catch (EOFException e){
            System.out.println("已经成功读取");
        } catch (ClassNotFoundException e){
            System.out.println("未找到该类");
            e.printStackTrace();
        }
    }
}
