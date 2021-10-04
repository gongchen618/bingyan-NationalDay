package server;

import com.alibaba.fastjson.JSON;
import message.Message;
import server.sql.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;

import static server.ServerCore.getStudentSockets;
import static server.ServerSqlHandler.changeStudentPassword;
import static server.ServerSqlHandler.getStudentById;

public class StudentMessageHandler extends Thread{
    private int studentId;
    private Message message;
    private Student student;

    StudentMessageHandler(int studentId, String str){
        this.studentId = studentId;
        this.message = JSON.parseObject(str, Message.class);
        this.student = getStudentById(studentId);
    }

    @Override
    public void run (){
        super.run();
        try {
            option ();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void option() throws IOException {
        switch (message.getType()){
            case "Password":
                changeStudentPassword(studentId, message.getContent());
                break;
            case "Query":
                System.out.println("【举手】@ " + student.getIdAndName() + "：" + message.getContent()
                        + " (port:" + getStudentSockets(studentId).getPort() + ")");
                break;
            case "Chat":
                Map<Integer, Socket> map = getStudentSockets();
                String msg = "@ " + student.getIdAndName() + "：" + message.getContent();
                for (Map.Entry<Integer, Socket> entry : map.entrySet()){
                    //if (entry.getKey().equals(studentId)) continue;
                    Socket socket = entry.getValue();
                    PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                    socketOutput.println(JSON.toJSONString(new Message("Chat", msg)));
                }
                break;
            default:
                System.out.println("[WARN] 传输信息格式错误 " + message);
        }
    }
}
