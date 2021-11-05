package View;

public interface PanelIf {
    //更改某些状态，需要重新绘制
    public void repaint();
    //增加一个字符串element
    public void addTextElement(String inputValue);

    //只能使用false！操作结束后使用
    public void setSelect(boolean s);
}
