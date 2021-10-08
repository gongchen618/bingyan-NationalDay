package client;

import com.alibaba.fastjson.JSON;
import message.Message;
import server.ServerCore;
import testfortest.MyScreenShot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

import static client.ClientCore.getScreenStaticTime;
import static client.ClientCore.setScreenStaticTime;
import static client.PictureCompare.getDHash;
import static client.PictureCompare.getHammingDistance;

public class ScreenStaticTimeAutoMachine extends Thread {

    private static String dHashLast = null;
    private Socket client;

    public ScreenStaticTimeAutoMachine (Socket client) {
        this.client = client;
    }

    @Override
    public void run (){
        super.run();
        try {
            option ();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void option() throws Exception {
        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);

        for (;;) {
            Robot robot = new Robot();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//获取屏幕的尺寸
            BufferedImage image = robot.createScreenCapture(
                    new Rectangle(0, 0, screenSize.width, screenSize.height));//截取整个屏幕的图像
            String dHashNow = getDHash(image);

            if (dHashLast == null || getHammingDistance(dHashNow, dHashLast) >= 5) {
                if (getScreenStaticTime() >= 3 * 12) {//更新移动的消息
                    socketPrintStream.println(JSON.toJSONString(new Message("Warn", String.valueOf(0))));
                }
                setScreenStaticTime(0);
            } else setScreenStaticTime(getScreenStaticTime() + 1);

            if (getScreenStaticTime() % 12 == 0) {
                int screenStaticTime = getScreenStaticTime() / 12;
                if (screenStaticTime >= 3) { //只有大于三分钟时才会按每分钟一次的频率更新
                    socketPrintStream.println(JSON.toJSONString(new Message("Warn", String.valueOf(screenStaticTime))));
                }
            }

            Thread.sleep(5000);
        }
    }

    public static String getdHashLast() {
        return dHashLast;
    }
    public static void setdHashLast(String dHashLast) {
        ScreenStaticTimeAutoMachine.dHashLast = dHashLast;
    }
}