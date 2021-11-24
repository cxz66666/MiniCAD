import java.util.*;

public class Main {

    public static void main(String[] args) {
        // write your code here
        Scanner scanner = new Scanner(System.in); // 创建Scanner对象
        csv csv=new csv();
        String split=", ";
        while(true){
            String  line=scanner.nextLine();
            if(line.equals("END")){
                break;
            }
            String[] l=line.split(", ");
            student s;
            if(l.length==2){
                s=new student(l[0],l[1]);
            } else {
                s=new student(l[0],l[1],l[2]);
            }
            csv.addStudent(s);
        }
        Collections.sort(csv.students);
        System.out.printf("%s%s","student id"+split,"name"+split);
        for(String m:csv.courses){
            System.out.printf("%s%s",m,split);
        }
        System.out.printf("%s","average");

        for(student s:csv.students){
            System.out.printf("\n%s%s",s.stuId+split,s.name+split);
            double score=0;
            int num=0;
            for(String m:csv.courses){
                if(s.scores.containsKey(m)){
                    score+=s.scores.get(m);
                    num++;
                    System.out.printf("%.1f",s.scores.get(m));
                }
                System.out.printf("%s",split);
            }
            System.out.printf("%.1f",score/num);
        }
    }
}


class student implements Comparable<student>{
    String name;
    String stuId;
    HashMap<String, Double>scores;
    public student(String stuId, String course,String score){
        this.stuId=stuId;
        this.scores = new HashMap<String,Double>();
        this.scores.put(course,Double.parseDouble(score));
    }
    public student(String stuId, String name) {
        this.name = name;
        this.stuId = stuId;
        this.scores = new HashMap<String,Double>();
    }
    public student() {
        this.scores = new HashMap<String,Double>();
    }

    @Override
    public int compareTo(student o) {
        return stuId.compareTo(o.stuId);
    }
}
class csv {
    TreeSet<String>courses;
    List<student>students;
    {
        courses=new TreeSet<>();
        students=new ArrayList<>();
    }

    public csv() {
    }
    public void addStudent(student stu){
        boolean found=false;
        for(student m:students){
            if(m.stuId.equals(stu.stuId)){
                found=true;
                if(m.name==null&&stu.name!=null){
                    m.name=stu.name;
                }
                for(String course:stu.scores.keySet()){
                    m.scores.put(course,stu.scores.get(course));
                }
            }
        }
        if(!found){
            students.add(stu);
        }
        for(String course:stu.scores.keySet()){
            courses.add(course);
        }
    }
}