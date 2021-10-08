package server;

import com.alibaba.fastjson.JSON;
import de.scravy.pair.Pair;
import de.scravy.pair.Pairs;
import message.Message;
import server.sql.Student;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static server.ServerCore.*;
import static server.ServerSqlHandler.*;

public class StudentMessageHandler {
    private int studentId;
    private Message message;
    private Student student;

    StudentMessageHandler(int studentId, String str){
        this.studentId = studentId;
        this.message = JSON.parseObject(str, Message.class);
        this.student = getStudentById(studentId);
    }

    public void option() throws IOException {
        Map<Integer, Socket> map = getStudentSockets();
        String str = null, msg = null;
        Pair<FileOutputStream, Integer> fileInfo = null;

        switch (message.getType()){
            case "Password":
                setCell (studentId, "password", message.getContent());
                break;
            case "Query":
                System.out.println("【私聊】@ " + student.getIdAndName() + "：" + message.getContent()
                        + " (port:" + getStudentSockets(studentId).getPort() + ")");
                break;
            case "Chat":
                msg = "【聊天室】@ " + student.getIdAndName() + "：" + message.getContent();
                for (Map.Entry<Integer, Socket> entry : map.entrySet()){
                    //if (entry.getKey().equals(studentId)) continue;
                    Socket socket = entry.getValue();
                    PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                    socketOutput.println(JSON.toJSONString(new Message("Chat", msg)));
                }
                System.out.println(msg);
                break;
            case "Document":
                fileInfo = getStudentFileStreams(studentId);
                if (fileInfo == null) {
                    String path = getStudentFileDirectory() + "/StudentsDocuments/" + studentId + "_" + message.getContent();

                    File file = new File(path);
                    File fileParent = file.getParentFile();
                    if (!fileParent.exists()) fileParent.mkdirs();
                    if (file.exists()) file.delete();
                    file.createNewFile();//有路径才能创建文件

                    FileOutputStream fileOutputStream = new FileOutputStream(path);
                    setStudentFileStreams(studentId, fileOutputStream, null);
                } else if (fileInfo.getSecond() == null) {
                    setStudentFileStreams(studentId, fileInfo.getFirst(), Integer.parseInt(message.getContent()));
                } else {
                    FileOutputStream fileOutputStream = fileInfo.getFirst();
                    Integer length = fileInfo.getSecond();

                    byte[] buff = Base64.getDecoder().decode(message.getContent());
                    length -= buff.length;

                    fileOutputStream.write(buff, 0, buff.length);
                    if (length == 0) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        setStudentFileStreams(studentId, null, null);
                        System.out.println(student.getIdAndName() + "上传了一个文件");
                    } else {
                        setStudentFileStreams(studentId, fileOutputStream, length);
                    }
                }
                break;
            case "Monitor":
                Pair<FileOutputStream, Integer> monitorInfo = getStudentMonitorStreams(studentId);
                if (monitorInfo == null) {

                    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
                    String path = getStudentFileDirectory() + "/MonitorShots/" + studentId + "_" + df.format(new Date ()) + ".png";

                    File file = new File(path);
                    File fileParent = file.getParentFile();
                    if (!fileParent.exists()) fileParent.mkdirs();
                    if (file.exists()) file.delete();
                    file.createNewFile();//有路径才能创建文件

                    FileOutputStream fileOutputStream = new FileOutputStream(path);
                    setStudentMonitorStreams(studentId, fileOutputStream, null);
                } else if (monitorInfo.getSecond() == null) {
                    setStudentMonitorStreams(studentId, monitorInfo.getFirst(), Integer.parseInt(message.getContent()));
                } else {
                    FileOutputStream fileOutputStream = monitorInfo.getFirst();
                    Integer length = monitorInfo.getSecond();

                    byte[] buff = Base64.getDecoder().decode(message.getContent());
                    length -= buff.length;

                    fileOutputStream.write(buff, 0, buff.length);
                    if (length == 0) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        setStudentMonitorStreams(studentId, null, null);
                        System.out.println(student.getIdAndName() + "的屏幕截图已保存");
                    } else {
                        setStudentMonitorStreams(studentId, fileOutputStream, length);
                    }
                }
                break;
            case "Warn":
                int screenStaticTime = Integer.parseInt(message.getContent());
                setCell (studentId, "SST", String.valueOf(screenStaticTime));
                if (screenStaticTime >= 3 && (screenStaticTime - 3) % 5 == 0) { //每 5 分钟是一个节点
                    System.out.println("[提示]" + student.getIdAndName() + "的屏幕已有 "
                            + screenStaticTime + " 分钟没有变化");
                }
                break;
            case "Mark":
                fileInfo = getStudentFileStreams(studentId);
                if (fileInfo == null) {
                    str = message.getContent();
                    String arr[] = str.split("_", 2);//前一段是测试名字，后一段是它交的文件名字
                    String path = getStudentFileDirectory() + "/Test/" + arr[0] + "/Student/" + studentId + "_" + arr[1];
                    setStudentTestNames(studentId, arr[0] + "_" + studentId + "_" + arr[1]);

                    File file = new File(path);
                    File fileParent = file.getParentFile();
                    if (!fileParent.exists()) fileParent.mkdirs();
                    if (file.exists()) file.delete();
                    file.createNewFile();//有路径才能创建文件

                    FileOutputStream fileOutputStream = new FileOutputStream(path);
                    setStudentFileStreams(studentId, fileOutputStream, null);
                } else if (fileInfo.getSecond() == null) {
                    setStudentFileStreams(studentId, fileInfo.getFirst(), Integer.parseInt(message.getContent()));
                } else {
                    FileOutputStream fileOutputStream = fileInfo.getFirst();
                    Integer length = fileInfo.getSecond();

                    byte[] buff = Base64.getDecoder().decode(message.getContent());
                    length -= buff.length;

                    fileOutputStream.write(buff, 0, buff.length);
                    if (length == 0) {
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        str = getStudentTestNames(studentId);

                        String arr[] = str.split("_", 2);//前一段是测试名字，后一段是它交的文件名字
                        String path = getStudentFileDirectory() + "/Test/" + arr[0] + "/";

                        BufferedReader br1 = new BufferedReader(new FileReader(new File(path + "ANSWER.txt")));
                        BufferedReader br2 = new BufferedReader(new FileReader(new File(path + "/Student/" + studentId + "_" + arr[1])));

                        int cnt = 0;
                        String br1Line = null, br2Line = null;
                        while ((br1Line = br1.readLine()) != null && (br2Line = br2.readLine()) != null) {
                            if (br1Line.equals(br2Line)) ++cnt;
                        }

                        setCell (studentId, arr[0], String.valueOf(cnt));
                        Socket socket = getStudentSockets(studentId);
                        PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                        socketOutput.println(JSON.toJSONString(new Message("Result", arr[0] + "_" + cnt)));

                        System.out.println(student.getIdAndName() + "提交了客观题 " + arr[0] + " 并答对 " + cnt + " 道题，成绩已记录");

                        setStudentFileStreams(studentId, null, null);
                        setStudentTestNames(studentId, null);
                    } else {
                        setStudentFileStreams(studentId, fileOutputStream, length);
                    }
                }

                break;
            case "Test":
                fileInfo = getStudentFileStreams(studentId);
                if (fileInfo == null) {
                    str = message.getContent();
                    String arr[] = str.split("_", 2);//前一段是测试名字，后一段是它交的文件名字
                    String path = getStudentFileDirectory() + "/Test/" + arr[0] + "/Student/" + studentId + "_" + arr[1];

                    File file = new File(path);
                    File fileParent = file.getParentFile();
                    if (!fileParent.exists()) fileParent.mkdirs();
                    if (file.exists()) file.delete();
                    file.createNewFile();//有路径才能创建文件

                    FileOutputStream fileOutputStream = new FileOutputStream(path);
                    setStudentFileStreams(studentId, fileOutputStream, null);
                } else if (fileInfo.getSecond() == null) {
                    setStudentFileStreams(studentId, fileInfo.getFirst(), Integer.parseInt(message.getContent()));
                } else {
                    FileOutputStream fileOutputStream = fileInfo.getFirst();
                    Integer length = fileInfo.getSecond();

                    byte[] buff = Base64.getDecoder().decode(message.getContent());
                    length -= buff.length;

                    fileOutputStream.write(buff, 0, buff.length);
                    if (length == 0) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        setStudentFileStreams(studentId, null, null);
                        System.out.println(student.getIdAndName() + "提交了一份主观题");
                    } else {
                        setStudentFileStreams(studentId, fileOutputStream, length);
                    }
                }
                break;
            default:
                System.out.println("[WARN] 传输信息格式错误 " + message);
        }
    }
}
