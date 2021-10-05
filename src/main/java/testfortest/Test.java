package testfortest;

import com.alibaba.fastjson.JSON;
import de.scravy.pair.Pair;
import de.scravy.pair.Pairs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import static com.sun.tools.attach.VirtualMachine.list;

public class Test {
    @org.junit.jupiter.api.Test
    public void PairToString (){
        URL url = this.getClass().getResource("");
        System.out.println(url.getFile());
        File file = new File(url.getFile()); //new File
        //File parentFile = file.getParentFile();
        //System.out.println(file.list());
        String[] haha = file.list();
        for (String str : haha) {
            System.out.println(str);
        }

        File file2 = null; //new File
        try {
            file2 = new File("").getCanonicalFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //File parentFile = file.getParentFile();
        //System.out.println(file.list());
        String[] haha2 = file2.list();
        for (String str : haha2) {
            System.out.println(str);
        }
    }
    /*
    public static void main(String[] args) throws AWTException, IOException {
        Robot robot=new Robot();
        //获取指定矩形区域的屏幕图像
        BufferedImage bufferedImage=robot.createScreenCapture(new Rectangle(100,100,500,500));
        File f=new File("D:\\save.jpg");
        OutputStream os=new FileOutputStream(f);
        ImageIO.write(bufferedImage, "jpg", os);
    }*/
}
