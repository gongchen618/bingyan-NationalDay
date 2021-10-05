package server;

import com.alibaba.fastjson.JSON;
import de.scravy.pair.Pair;
import de.scravy.pair.Pairs;
import message.Message;
import server.sql.Student;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static server.ServerCore.*;
import static server.ServerSqlHandler.changeStudentPassword;
import static server.ServerSqlHandler.getStudentById;

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
        String msg = null;

        switch (message.getType()){
            case "Password":
                changeStudentPassword(studentId, message.getContent());
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
                Pair<FileOutputStream, Integer> fileInfo = getStudentFileStreams(studentId);
                if (fileInfo == null) {
                    String path = "UploadFromStudents/" + studentId + "_" + message.getContent();

                    File file = new File(path);
                    File fileParent = file.getParentFile();
                    if (!fileParent.exists()) fileParent.mkdirs();
                    if (file.exists()) file.delete();
                    file.createNewFile();//有路径才能创建文件

                    FileOutputStream fileOutputStream = new FileOutputStream(path);
                    setStudentFileStreams(studentId, fileOutputStream, null);

                    InputStream in = System.in;
                    BufferedReader input = new BufferedReader(new InputStreamReader(in));//从键盘读入

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
            default:
                System.out.println("[WARN] 传输信息格式错误 " + message);
        }
    }
}
