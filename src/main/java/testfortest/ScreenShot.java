package testfortest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;

public class ScreenShot {
    /*
    public static String screenShot(String fileName) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//获取屏幕大小
        Rectangle screenRectangle = new Rectangle(screenSize);//根据屏幕大小创建一个矩形
        String randomName = UUID.randomUUID().toString().replace("-", "");
        String name = randomName + ".png";//jpg等也可
        // 截图保存的路径
        File screenFile = new File(fileName);
        if (!screenFile.exists()) {
            screenFile.mkdirs();//创建文件路径
        }
        Robot robot;
        String path = "";
        boolean b = false;
        try {
            robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);//使用Robot类提供的截屏方法，
            File f = new File(screenFile, name);
            b = ImageIO.write(image, "png", f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (b) {
            path = fileName + File.separator + name;
        }
        return path;
    }*/
}