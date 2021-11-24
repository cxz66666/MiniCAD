package Utils;


import Model.ElementManager;

import java.io.*;

public class File {
    private static final String sepa = java.io.File.separator;

    public static void open(String path,String file) throws FileNotFoundException, IOException {
        ObjectInputStream in=new ObjectInputStream(new FileInputStream(path+sepa+file));
        ElementManager.getInstance().read(in);
    }

    public static void save(String path,String file) throws IOException{
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(path+sepa+file));
        ElementManager.getInstance().write(outputStream);
        outputStream.flush();
        outputStream.close();
        return;
    }
}
