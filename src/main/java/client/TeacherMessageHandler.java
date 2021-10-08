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

import de.scravy.pair.Pair;
import de.scravy.pair.Pairs;

import static client.ClientCore.*;
import static java.lang.Math.min;
import static server.ServerCore.getStudentSockets;

public class TeacherMessageHandler {
    private Message message;
    private Socket client;

    TeacherMessageHandler (String str, Socket client){
        this.message = JSON.parseObject(str, Message.class);
        this.client = client;
    }

    public void option() throws Exception {
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
            case "Result":
                String arr_[] = message.getContent().split("_", 2);//前一段是测试名字，后一段是分数
                System.out.println("【公告】你在测试 " + arr_[0] + " 中得分 " + arr_[2]);
                break;
            case "Test"://test + "_" + file.getName()
            case "Mark":
                String arr[] = message.getContent().split("_", 2);//前一段是测试名字，后一段是文件名字

                Pair<FileOutputStream, Integer> fileInfo = getFileFromTeacher();
                if (fileInfo == null) {
                    String path = new File("").getCanonicalPath() + "/Test/" + message.getContent();

                    File file = new File(path);
                    File fileParent = file.getParentFile();
                    if (!fileParent.exists()) fileParent.mkdirs();
                    if (file.exists()) file.delete();
                    file.createNewFile();//有路径才能创建文件

                    FileOutputStream fileOutputStream = new FileOutputStream(path);
                    setFileFromTeacher(fileOutputStream, null);

                    if (message.getType().equals("Mark")) {
                        System.out.println("【公告】正在接收新的客观题测试 " + arr[0]);
                        setFileType(arr[0], false);
                    } else {
                        System.out.println("【公告】正在接收新的主观题测试 " + arr[0]);
                        setFileType(arr[0], true);
                    }
                } else if (fileInfo.getSecond() == null) {
                    setFileFromTeacher(fileInfo.getFirst(), Integer.parseInt(message.getContent()));
                } else {
                    FileOutputStream fileOutputStream = fileInfo.getFirst();
                    Integer length2 = fileInfo.getSecond();

                    byte[] buff2 = Base64.getDecoder().decode(message.getContent());
                    length2 -= buff2.length;

                    fileOutputStream.write(buff2, 0, buff2.length);
                    if (length2 == 0) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        setFileFromTeacher(null, null);

                        if (message.getType().equals("Mark")) {
                            System.out.println("【公告】一份新的客观题测试接收完毕，请及时查看并提交");
                        } else {
                            System.out.println("【公告】一份新的主观题测试接收完毕，请及时查看并提交");
                        }
                    } else {
                        setFileFromTeacher(fileOutputStream, length2);
                    }
                }
                break;
            case "Bye":
                setFlagIsLogIn(false);
                break;
            default:
                System.out.println("[WARN] 传输信息格式错误 " + message);
        }
    }
}