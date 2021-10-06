package client;

import com.alibaba.fastjson.JSON;
import message.Message;
import server.sql.Student;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.Map;

import static client.ClientCore.setFlagIsLogIn;
import static java.lang.Math.min;
import static server.ServerCore.getStudentSockets;

public class TeacherMessageHandler extends Thread{
    private Message message;
    private Socket client;

    TeacherMessageHandler (String str, Socket client){
        this.message = JSON.parseObject(str, Message.class);
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
        switch (message.getType()){
            case "Chat":
                System.out.println(message.getContent());
                break;
            case "BroadCast":
                System.out.println("【广播】@ 老师：" + message.getContent());
                break;
            case "Answer":
                System.out.println("【私聊】@ 老师：" + message.getContent());
                break;
            case "Monitor":
                Robot robot = new Robot();
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                BufferedImage image = robot.createScreenCapture(
                        new Rectangle(0, 0,screenSize.width, screenSize.height));

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", byteArrayOutputStream);
                byteArrayOutputStream.flush();
                byte[] buff = byteArrayOutputStream.toByteArray();
                int length = buff.length;

                OutputStream outputStream = client.getOutputStream();
                PrintStream socketPrintStream = new PrintStream(outputStream);//客户端输出

                socketPrintStream.println(JSON.toJSONString(new Message("Monitor", "screenshot")));
                socketPrintStream.println(JSON.toJSONString(new Message("Monitor", String.valueOf(length))));

                for (int left = 0, MAXLEN = 2048; left < length; left += MAXLEN) {
                    int nowLength = min (MAXLEN, length - left);
                    byte[] trueBuff = new byte[nowLength];
                    System.arraycopy(buff, left, trueBuff, 0, nowLength);
                    socketPrintStream.println(JSON.toJSONString(new Message("Monitor", Base64.getEncoder().encodeToString(trueBuff))));
                }
                System.out.println("文件已发送");
                break;
            case "Bye":
                setFlagIsLogIn(false);
                break;
            default:
                System.out.println("[WARN] 传输信息格式错误 " + message);
        }
    }
}