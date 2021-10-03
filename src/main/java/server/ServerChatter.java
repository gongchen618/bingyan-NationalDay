package server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.xdevapi.JsonParser;
import server.sql.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import static server.ServerGetSql.changeStudentStatus;
import static server.ServerGetSql.getStudentById;

//import static server.ServerGetSql.getStudentById;

public class ServerChatter {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8888);

        System.out.println("教师端已启动");
        System.out.println("教师端：" + server.getInetAddress() + ":" + server.getLocalPort());

        for(;;) {
            Socket client = server.accept();
            ClientHandler clientHandler = new ClientHandler(client);
            clientHandler.start();
        }
    }

    private static class ClientHandler extends Thread{
        private Socket socket;

        ClientHandler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新学生端启动：" + socket.getInetAddress() + ":" + socket.getPort());

            try{
                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                /*
                socketOutput.println("");//向学生端输出
                String str = socketInput.readLine();//从学生端输入
                 */

                boolean flagConnect = false;

                do {
                    String str = socketInput.readLine();
                    Student student = JSON.parseObject(str, Student.class);
                    //System.out.println(student.toString());

                    Student studentInfo = getStudentById(student.getStudentId());
                    //System.out.println(studentInfo.toString());
                    if (studentInfo == null) {
                        socketOutput.println("学号不存在！");
                    } else if (studentInfo.getPassword().equals(student.getPassword())) {
                        socketOutput.println("Login Successfully");
                        flagConnect = true;
                        System.out.println(studentInfo.getName() + " 登录成功！");
                        changeStudentStatus(student.getStudentId(), "Online");
                    } else {
                        socketOutput.println("密码错误！");
                    }
                } while (!flagConnect);

                socketInput.close();
                socketOutput.close();
                /*
                do{
                    String str = socketInput.readLine();//客户端输入
                    if ("bye".equalsIgnoreCase(str)) {
                        flag = false;
                        socketOutput.println("bye");
                    } else {
                        System.out.println(str);
                        socketOutput.println("接受到文件大小：" + str.length());
                    }

                } while (flag);
                */

            }catch (Exception e){
                e.printStackTrace();
                System.out.println("学生端连接异常断开："+ socket.getInetAddress() + ":" + socket.getPort());
            } finally {
                try {
                    socket.close();
                    System.out.println("学生端断开："+ socket.getInetAddress() + ":" + socket.getPort());
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}