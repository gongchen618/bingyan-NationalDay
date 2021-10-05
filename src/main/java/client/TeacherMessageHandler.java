package client;

import com.alibaba.fastjson.JSON;
import message.Message;
import server.sql.Student;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import static server.ServerCore.getStudentSockets;

public class TeacherMessageHandler extends Thread{
    private Message message;

    TeacherMessageHandler (String str){
        this.message = JSON.parseObject(str, Message.class);
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
            case "Chat":
                System.out.println(message.getContent());
                break;
            case "BroadCast":
                System.out.println("【广播】@ 老师：" + message.getContent());
                break;
            case "Answer":
                System.out.println("【私聊】@ 老师：" + message.getContent());
                break;
            default:
                System.out.println("[WARN] 传输信息格式错误 " + message);
        }
    }
}