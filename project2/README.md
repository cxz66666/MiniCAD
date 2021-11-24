#  MiniCAD 实验报告



### 1.实验要求

​	做一个简单的绘图工具，以CAD的方式操作，能放置直线、矩形、圆和文字，能选中图形，修改参数，如颜色等，能拖动图形和调整大小，可以保存和恢复。



### 2.实验设计

#### 2.1 设计架构

​	首先我们整体架构设计采用MVC（Model--View--Control）的三层架构，三者相互作用，同时易于开发和维护，但是在实现过程中，我们发现如果Control单独放置一个package，因为很多control功能大体类似，则会导致逻辑琐碎、命名困难等问题，因此我们把control基本全部设计成了匿名类的形式，融合到View的代码中，方便思路的在整合，同时我们额外增加一个`Utils` package，用于放置辅助工具的代码。



#### 2.2 界面结构

​	整个界面结构我们采用`BorderLayout`，最上侧为一个`MenuBar`，左侧为画板`panel`区域，中间为`ToolBar`区域，由于没有右侧，中间的部分会靠右排序，而我们又将`ToolBar`部分大小固定下来，画板可以正确的占据大多数屏幕范围。

​	在ToolBar内部，我们采用`GridLayout`布局，一行五列，前四列都是用`JButton`，内部Icon为图片，最后一列由于需要放置颜色板，因此又采用了`3*4`的的`GridLayout`布局，因此整体结构如下图所示：

![image-20211105135633336](https://pic.raynor.top/images/2021/11/05/image-20211105135633336.png)



#### 2.3 功能设计：

​	本次设计最大的难点在于中间两个重要组件`Panel`和`ToolBar`之间进行通信与数据共享的方法，我们当然可以**简单粗暴的将`Panel`直接放到`ToolBar`内部**，但是！这样会导致高度的耦合以及大量代码杂糅，无法方便的开发和维护，因此我们设计并实现了`PanelIf`和`ToolbarIf`两个接口，这两个组件分别实现该接口，同时拥有对方接口的一个实例（即接口的实例互相设置为对方的实例即可，两者公共父组件实现时进行设置），这样可以很方便的解耦合，如下是两个接口的设计：

PanelIf:

```java
public interface PanelIf {
    //更改某些状态，需要重新绘制
    public void repaint();
    //增加一个字符串element
    public void addTextElement(String inputValue);

    //只能使用false！操作结束后使用
    public void setSelect(boolean s);
}

```



ToolIf:

```java
public interface ToolIf {
    //获取当前Button选择
    public Type getButton();
    //将Button选择置位
    public void setButtonNone();
}

```

因此两者可以方便的进行数据共享

​	同时MenuBar比较简单，这里不再赘述，提供了保存、打开、退出三个功能，同时分别绑定了快捷键方便操作。





#### 2.4 Model设计

​	利用java的类与继承机制，我们可以很方便的设计出一套通用的父类`Element`，该父类为抽象类，提供了如下的抽象函数

~~~java
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
    //变粗
    public void wider(){
    }
	//变细
    public void thinner(){

    }
    public void setColor(Color c) {
    }
	//设置当前选中状态
    public void setSelect(boolean status){
       
    }
    public void draw(Graphics2D g){
    }
~~~



如下是我们实现的若干图形：

- **直线**

  直线的实现非常简单，只有两个Point，一个开始一个结束，同时各种操作也比较好实现，这里我们只展示部分重要函数的代码

  ~~~java
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
  ~~~

  

- **长方形**

  长方形实现需要两个点，一个为开始时候的点p1，一个是当前左上角的点p2，以及with和height，开始时候p1=p2，但是当我们还没有松开鼠标而继续修改长方形形状的时候，很有可能我们直接把整个长方形反过来了，为了在这种情况下我们仍然能够画出该图像，我们需要额外记录p1点，同时和p2进行比较即可得到当前的状态

  ~~~java
      @Override
      public void prolong() {
  
          width+=width/height;
          height+=1;
      }
      @Override
      public boolean inElement(Point p) {
          if(p.x>=x&&p.x<=x+width&p.y>=y&&p.y<=y+height){
              select=true;
              return true ;
          }
          return false;
      }
  ~~~

  

- **椭圆**

  椭圆和长方形非常类似，这里我们不再赘述

  ~~~java
      @Override
      public void prolong() {
          width+=width/height;
          height+=1;
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
  ~~~

  

- **文字**

  文字比较麻烦的一点在于需要记录文字的宽和高，这里我们记录下该图像使用的`FontMetrics`和`Font`，即可通过`fm.stringWidth(content)`以及`fm.getHeight()`获取该content需要的长和宽，之后操作大体类似：

  ~~~java
      public void prolong() {
          font = new Font("SansSarif", Font.BOLD, font.getSize() + 1);
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
  ~~~

  

#### 2.5 Element单例模式

​	在管理Element的类`ElementManager`中，我们采用了单例模式，保证整个程序运行时只有一个该类被产生，因此大大简化了整体逻辑流程，我们在该类中提供了大量供其他类调用的函数，注意其他任何类都不能直接对该单例模式中的类进行修改操作，**必须通过Manager提供的函数进行修改**，这样保证了修改的可控性：

~~~java
	//返回list
    public List<Element>getList() {
    }
	//增加单个元素
    public void Add(Element element){
    }
    //删除单个元素
    public void Remove(Element element){
      
    }
	//删除所有元素
    public void ClearAll(){
    }

    //将所有状态设置为false
    public void resetSelect(){
    }
    // 如果该point落在某element范围内，则改element被设置为选中状态，返回改element
    // 否则所有element的select都置为false，同时返回null
    public Element CheckSelect(Point point){
    }

    public void delete(){

    }

    public void write(ObjectOutputStream out) throws IOException {

    }
    public void read(ObjectInputStream in) throws IOException {

    }
~~~



#### 2.6 工具类

​	在Utils包下我们提供了一些工具函数，包括判定选中与否，读取 or 保存文件、所有的常量定义等，这里不再赘述

​	在文件存储与读取上，为了简单起见我们使用了**Java序列化机制**，直接让Element继承Serializable接口，即可将其声明为可序列化的，因此我们只需要打开一个序列化流并将当前单例里的所有element写入/读取出来即可。

~~~java
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
~~~

注意一些错误处理机制，我们这里只是简单的进行打印

在判定文件是否读完的情况下，我们直接对EOFException异常进行捕获，如果捕获成功代表该文件已经读取完毕！









### 3.实验结果

#### 3.1 实现的功能

在菜单栏能够选择打开、保存、退出；在工具栏可以选择 直线、椭圆、长方形、文字框，颜色更改工具；画板

- 单击图形可以选中图形，并拖动图形
- 选中图形后按`R`或`r`键可以删除选中图形，如果没有选中图形则不做任何操作
- 选中图形按`+`或`=`键可以变长该选中的图形，按`-`或`_`可以缩短该选中的图形，如果没有选中图形则不做任何操作
- 选中图形按`>`或`.`键可以加粗该选中的图形，按`<`或`,`可以变细该选中的图形，如果没有选中图形则不做任何操作
- 实现画线 、画椭圆 、画矩形和文本框
- 选中图形后，点击颜色可以更改该图形的颜色，如果未选中不做任何操作
- 菜单栏的文件选项中能打开、保存文件以及退出程序

具体简单的绘制我们提供了demo文件 demo.cad，可以使用`打开`功能打开该文件并观察，显示内容如下

![image-20211105144052968](https://pic.raynor.top/images/2021/11/05/image-20211105144052968.png)



3.1.2 其他功能

​	实现了菜单栏的快捷键设置，按 Alt+F 可以打开菜单栏的文件菜单。Ctrl+O 打开文件、Ctrl+S 保存文件、Ctrl+E 退出程序。

​	需要注意的是，选择的图形是只生效一次的，也就是说如果你要连续画两个矩形，则需要点两次矩形按钮，在画完一个图形后图形选择即消失。





### 4.实验心得

由于基本没有使用过 Java 的 GUI，所以我对这种Client端的设计一直不太熟悉。在开始做这个实验时，感觉实现起来非常简单，但是就是不知道怎么把每个对象展示。于是在前期，我选择先实现较为抽象的element类，也就是Model层的内容，这一部分基本上和其他两层是没有耦合的；在后面才实现各个按钮对应的功能，以及画布的绘制。基本都是google相应的demo，然后自己去理解后再使用到实验中。

在整体结构的设计上，我也思考了很长时间，一直希望能将MVC三层真正分开，但是在本次实验中如果要三层分开，需要增加的代码量会非常大，而且因为逻辑差异小、命名难、不易维护，因此我们最终还是选择将逻辑嵌入到了view层之中，同时使用单例模式、组件间状态共享等方法，感觉自己确实学到了很多设计结构的内容。

