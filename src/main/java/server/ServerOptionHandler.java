package server;

import com.alibaba.fastjson.JSON;
import message.Message;
import server.sql.Student;

import java.util.*;

import java.io.*;
import java.net.Socket;

import static server.ServerCore.*;
import static server.ServerSqlHandler.*;

public class ServerOptionHandler extends Thread {

    private void printOptionList (){
        System.out.println("[Opt0]切换班级  " +
                "[Opt1]重置/修改学生状态  " +
                "[Opt2]标记上课/下课  " +
                "[Opt3]查看课堂情况  " +
                "[Opt4]发送消息  \n" +
                "[Opt5]修改接收文件夹路径  " +
                "[Opt6]查看学生屏幕  " +
                "[Opt7]批量导入学生名单  " +
                "[Opt8]删除班级  \n" +
                "[Opt9]发放测试(新建/单独补发)  " +
                "[Opt10]手动批改测试  " +
                "[Opt11]查看学生测试成绩  " +
                "[Opt12]删除学生  " +
                "[Opt13]删除测试");
    }

    public ServerOptionHandler() {
    }

    @Override
    public void run() {
        super.run();
        try{
            option();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void option() throws IOException{

        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));//从键盘读入
        Map <Integer, Socket> map = null;
        int studentId = 0;
        Student student = null;
        Socket socket = null;
        String test = null, path = null;

        for (;;) {
            printOptionList();
            String str = input.readLine().toLowerCase();
            switch (str) {
                case "opt0": //切换班级
                    map = getStudentSockets();
                    if (map.isEmpty() == false) {
                        System.out.println("[警告]当前班级有学生处于登录状态，切换班级会使它们断开连接");
                        System.out.println("确认继续吗？[y/N]");
                        str = input.readLine().toLowerCase();
                        if (str.equals("y") == false) {
                            System.out.println("操作已取消");
                            break;
                        }
                    }
                    printTableName();
                    System.out.print("请输入新的班级：");
                    str = input.readLine();
                    if (!isTableExist(str)) {
                        System.out.println("该班级不存在！");
                        break;
                    } else if (str.equals(getClassId())) {
                        System.out.println("不能重复切换为当前班级！");
                    } else {
                        for (Map.Entry<Integer, Socket> entry : map.entrySet()){
                            PrintStream socketOutput = new PrintStream(entry.getValue().getOutputStream());//学生输出
                            socketOutput.println(JSON.toJSONString(new Message("Bye", "")));
                        }
                        clearStudentSockets();
                        setClassId(str);
                    }
                    break;
                case "opt1": //修改学生状态
                    for (boolean flag = false; !flag; ) {
                        System.out.println("[Opt0]强制重置所有学生状态为 \"缺席\"  [Opt1]修改单个学生状态  [Opt2]取消操作");

                        str = input.readLine().toLowerCase();
                        flag = true;

                        if (str.equals("opt0")) resetColumn("performance");
                        else if (str.equals("opt1")) {
                            System.out.print("请输入目标学生学号：");
                            try {
                                studentId = Integer.parseInt(input.readLine());
                            } catch (Exception e){
                                System.out.println("输入格式非法！");
                                break;
                            }
                            if (isStudentExist(studentId)) {
                                student = getStudentById(studentId);
                                System.out.println(student.getIdAndName() + "当前状态为 " + student.getPerformance());
                                System.out.println("[Opt0]正常  " +
                                        "[Opt1]缺席  " +
                                        "[Opt2]迟到  " +
                                        "[Opt3]早退");
                                System.out.print("请输入新的状态编号：");
                                str = input.readLine().toLowerCase();
                                if (str.equals("opt0")) str = "正常";
                                else if (str.equals("opt1")) str = "缺席";
                                else if (str.equals("opt2")) str = "迟到";
                                else if (str.equals("opt3")) str = "早退";
                                else {
                                    System.out.println(" 操作无效，请重新输入");
                                    break;
                                }
                                setCell(studentId, "performance", str);
                            } else {
                                System.out.println("该学生不存在！");
                            }
                        } else if (str.equals("opt2")) {
                            System.out.println("操作已取消");
                        } else {
                            System.out.println(" 操作无效，请重新输入");
                            flag = false;
                        }
                    }
                    break;
                case "opt2": //标记上课下课
                    for (boolean flag = false; !flag; ) {
                        setClassTime(getClassTime());
                        System.out.println("[Opt0]开始上课(开始登记迟到)  [Opt1]下课(学生状态定格)  [Opt2]取消操作");

                        str = input.readLine();
                        flag = true;

                        if (str.equals("opt0")) {
                            setClassTime(1);
                        }
                        else if (str.equals("opt1")) {
                            setClassTime(2);
                        }
                        else if (str.equals("opt2")) {
                            System.out.println("操作已取消");
                        }
                        else {
                            System.out.println(str + " 操作无效，请重新输入");
                            flag = false;
                        }
                    }
                    break;
                case "opt3": //打印课堂状态
                    printStudentListByOrder();
                    break;
                case "opt4": //向学生发送消息
                    for (boolean flag = false; !flag; ) {
                        System.out.println("[Opt0]向单个学生发送  [Opt1]向全体学生广播  [Opt2]聊天室  [Opt3]取消操作");

                        str = input.readLine().toLowerCase();
                        flag = true;

                        if (str.equals("opt0")) {
                            System.out.print("请输入目标学生学号：");
                            try {
                                studentId = Integer.parseInt(input.readLine());
                            } catch (Exception e){
                                System.out.println("输入非法！");
                                break;
                            }
                            if (isStudentExist(studentId) == true) {
                                socket = getStudentSockets(studentId);
                                if (socket == null) {
                                    System.out.println("该学生未登录！");
                                } else {
                                    System.out.print("请输入消息内容：");
                                    str = input.readLine();

                                    PrintStream socketOutput = new PrintStream(socket.getOutputStream());//学生输出
                                    socketOutput.println(JSON.toJSONString(new Message("Answer", str)));
                                    System.out.println("消息已发送");
                                }
                            } else {
                                System.out.println("该学生不存在！");
                            }
                        } else if (str.equals("opt1")) {
                            System.out.print("请输入广播消息内容：");
                            str = input.readLine();

                            map = getStudentSockets();
                            for (Map.Entry<Integer, Socket> entry : map.entrySet()) {
                                socket = entry.getValue();
                                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                                socketOutput.println(JSON.toJSONString(new Message("BroadCast", str)));
                            }

                            System.out.println("消息已发送");
                        } else if (str.equals("opt2")) {
                            System.out.print("请输入聊天室消息内容：");
                            str = "【聊天室】@ 老师：" + input.readLine();

                            map = getStudentSockets();
                            for (Map.Entry<Integer, Socket> entry : map.entrySet()) {
                                socket = entry.getValue();
                                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                                socketOutput.println(JSON.toJSONString(new Message("Chat", str)));
                            }

                            //System.out.println("消息已发送");
                            System.out.println(str);
                        } else if (str.equals("opt3")) {
                            System.out.println("操作已取消");
                        } else {
                            System.out.println("操作无效，请重新输入");
                            flag = false;
                        }
                    }
                    break;
                case "opt5"://修改文件夹
                    System.out.println("当前接收文件夹路径：" + getStudentFileDirectory());
                    System.out.print("确认要修改路径吗(已接收文件不会移动)[y/N]：");
                    str = input.readLine().toLowerCase();
                    if (str.equals("y")) {
                        System.out.print("请输入新的接收文件夹完整路径：");
                        str = input.readLine();
                        File directory = new File(str);
                        if (!directory.exists()) directory.mkdirs();
                        setStudentFileDirectory(str);
                        System.out.println("文件存放路径已修改！");
                    } else System.out.println("操作已取消");
                    break;
                case "opt6"://远程截图
                    System.out.print("请输入目标学生学号：");
                    try {
                        studentId = Integer.parseInt(input.readLine());
                    } catch (Exception e){
                        System.out.println("输入非法！");
                        break;
                    }
                    if (isStudentExist(studentId) == true) {
                        socket = getStudentSockets(studentId);
                        if (socket == null) {
                            System.out.println("该学生未登录！");
                        } else {
                            PrintStream socketOutput = new PrintStream(socket.getOutputStream());//学生输出
                            socketOutput.println(JSON.toJSONString(new Message("Monitor", "")));
                            System.out.println("正在截图...");
                        }
                    } else {
                        System.out.println("该学生不存在！");
                    }
                    break;
                case "opt7"://导入新的学生
                    System.out.print("请输入班级名：");
                    str = input.readLine();
                    if (isTableExist(str)) {
                        System.out.println("该班级已存在！");
                        System.out.println("[Opt0]覆盖原有班级  [Opt1]在班级末尾新增  [Opt2]取消操作");
                        String tmp = input.readLine().toLowerCase();
                        if (tmp.equals("opt0")) {
                            createNewClass(str);
                            System.out.println("原班级已被覆盖");
                        } else if (tmp.equals("opt2")) {
                            System.out.println("操作已取消");
                            break;
                        }
                    } else {
                        createNewClass(str);
                        System.out.println("已为班级 " + str + " 建立新的数据库");
                    }

                    System.out.println("请依次输入学生的学号和姓名，学号和姓名之间由空格隔开" +
                            "，支持批量插入，请不要有空行，输入完成时请在新的一行输入 \"end\"");

                    List<Student> list = new ArrayList<>();
                    try {
                        int cnt = 0;
                        for (; ; ) {
                            String studentInfo = input.readLine();
                            if (studentInfo.equals("end")) break;

                            cnt += 1;
                            String arr[] = studentInfo.split("\\s+");
                            list.add(new Student(Integer.parseInt(arr[0]), "\"" + arr[1] + "\""));
                        }
                        if (list.isEmpty()) {
                            System.out.println("[警告]新建班级为空");
                        } else {
                            System.out.println("本次一共新增 " + cnt + " 个学生");
                            insertNewStudents(str, list);
                        }
                    } catch (Exception e){
                        System.out.println("[警告]输入格式非法！");
                    }
                    break;
                case "opt8": //删除班级
                    printTableName();
                    System.out.print("请输入要删除的班级：");
                    str = input.readLine();
                    if (str.equals(getClassId())) {
                        System.out.println("不可删除当前班级！");
                    } else if (isTableExist(str)) {
                        deleteOldClass(str);
                        System.out.println("班级 " + str + " 已删除！");
                    } else {
                        System.out.println("目标班级不存在！");
                    }
                    break;
                case "opt9": //布置测试(分数应当反馈给学员)
                    System.out.println("[Opt1]向单个学生补发已有测试  [Opt2]创建新的测试并发送给所有在线学生");
                    str = input.readLine().toLowerCase();

                    if (str.equals("opt1")) {
                        try {
                            System.out.print("请输入学生学号：");
                            str = input.readLine();
                            studentId = Integer.parseInt(str);
                            if (isStudentExist(studentId) == true) {
                                socket = getStudentSockets(studentId);

                                if (socket == null) {
                                    System.out.println("该学生未登录！");
                                } else {
                                    if (printTestNameList() == false) break;

                                    System.out.print("请输入需要补发的测试名称：");
                                    str = input.readLine();
                                    test = str;
                                    if (isTestExist(str) == false) {
                                        System.out.println("测试不存在！");
                                        break;
                                    }

                                    path = getStudentFileDirectory() + "/Test/" + test + "/";
                                    File directory = new File(path);
                                    if (!directory.exists()) directory.mkdirs();

                                    String type = null;
                                    if (getFileType(test) != null) {
                                        if (getFileType(test).equals(false)) type = "Mark";
                                        else type = "Test";
                                    } else {
                                        System.out.println("[Opt0]客观题(请准备好答案文件，程序会自动计算得分)  [Opt1]主观题");
                                        System.out.print("当前测试未存档，请手动选择测试类型：");

                                        str = input.readLine().toLowerCase();
                                        if (str.equals("opt0")) type = "Mark";
                                        else if (str.equals("opt1")) type = "Test";
                                        else {
                                            System.out.println("操作无效");
                                            break;
                                        }
                                    }

                                    for (;;) {
                                        System.out.print("请输入题面文件全称(文件应放置在 " + path + " 内)：");
                                        str = input.readLine();

                                        File file = new File(path + str);
                                        if (file.exists()) {
                                            if (type.equals("Test")) break;
                                            else if ((new File (path + "ANSWER.txt")).exists()) break;
                                            else {
                                                System.out.println("[错误]客观题文件夹没有正确放置 ANSWER.txt！");
                                            }
                                        } else {
                                            System.out.println("题面文件不存在！[提示]可作为题面的文件包括：");
                                            String[] documents = (new File(path)).list();
                                            for (String document : documents) {
                                                System.out.print(document + "  ");
                                            }
                                            System.out.println("");
                                        }
                                    }

                                    File file = new File (path + str);
                                    student = getStudentById(studentId);

                                    socket = getStudentSockets(studentId);
                                    PrintStream socketOutput = new PrintStream(socket.getOutputStream());

                                    FileInputStream fileInputStream = new FileInputStream(path + str);

                                    byte[] buff = new byte[2048];
                                    socketOutput.println(JSON.toJSONString(new Message(type, test + "_" + file.getName())));
                                    socketOutput.println(JSON.toJSONString(new Message(type, String.valueOf(file.length()))));

                                    int length = 0;
                                    while ((length = fileInputStream.read(buff)) != -1) {
                                        byte[] trueBuff = new byte[length];
                                        System.arraycopy(buff, 0, trueBuff, 0, length);
                                        socketOutput.println(JSON.toJSONString(new Message(type, Base64.getEncoder().encodeToString(trueBuff))));
                                    }

                                    System.out.println("测试 " + test + " 的题面已补发给 " + student.getIdAndName());
                                }
                            } else {
                                System.out.println("该学生不存在！");
                            }
                        } catch (Exception e){
                            System.out.println("非法输入！");
                        }
                        break;
                    } else if (str.equals("opt2") == false){
                        System.out.println("操作无效");
                        break;
                    }

                    System.out.print("请输入新的测试名称：");
                    str = input.readLine();
                    test = str;
                    if (isTestExist(str)) {
                        System.out.println("创建失败！已存在同名测试");
                        break;
                    }
                    path = getStudentFileDirectory() + "/Test/" + test + "/";
                    File directory = new File(path);
                    if (!directory.exists()) directory.mkdirs();

                    System.out.println("[Opt0]客观题(请准备好答案文件，程序会自动计算得分)  [Opt1]主观题");
                    System.out.print("请选择测试类型：");

                    String type = null;
                    str = input.readLine().toLowerCase();
                    if (str.equals("opt0")) {
                        type = "Mark";
                        System.out.println("[提示]你选择客观题，请在相同路径放置 ANSWER.txt，并在第 n 行保存第 n 个小空的唯一答案，注意不得有多余空格 / 空行");
                    }
                    else if (str.equals("opt1")) type = "Test";
                    else {
                        System.out.println("操作无效");
                        break;
                    }

                    System.out.println("测试已建立，请将测试题面文件放在 " + path + " 内");
                    for (;;) {
                        System.out.print("确认放置完毕后请输入题面文件全称：");
                        str = input.readLine();

                        File file = new File(path + str);
                        if (file.exists()) {
                            if (type.equals("Test")) break;
                            else if ((new File (path + "ANSWER.txt")).exists()) break;
                            else {
                                System.out.println("[错误]客观题文件夹没有正确放置 ANSWER.txt！");
                            }
                        } else {
                            System.out.println("题面文件不存在！[提示]可作为题面的文件包括：");
                            String[] documents = (new File(path)).list();
                            for (String document : documents) {
                                System.out.print(document + "  ");
                            }
                            System.out.println("");
                        }
                    }

                    map = getStudentSockets();
                    File file = new File (path + str);
                    for (Map.Entry<Integer, Socket> entry : map.entrySet()){
                        socket = entry.getValue();
                        PrintStream socketOutput = new PrintStream(socket.getOutputStream());

                        FileInputStream fileInputStream = new FileInputStream(path + str);

                        byte[] buff = new byte[2048];
                        socketOutput.println(JSON.toJSONString(new Message(type, test + "_" + file.getName())));
                        socketOutput.println(JSON.toJSONString(new Message(type, String.valueOf(file.length()))));

                        int length = 0;
                        while ((length = fileInputStream.read(buff)) != -1) {
                            byte[] trueBuff = new byte[length];
                            System.arraycopy(buff, 0, trueBuff, 0, length);
                            socketOutput.println(JSON.toJSONString(new Message(type, Base64.getEncoder().encodeToString(trueBuff))));
                        }
                    }

                    if (type.equals("Mark")) setFileType(test, false);
                    else setFileType(test, true);
                    createNewTest(test);
                    System.out.println("测试 " + test + " 的题面已下发");

                    break;
                case "opt10": //手动批改测试 //这里应该给学生反馈成绩但是没时间写了
                    if (printTestNameList() == false) break;
                    System.out.print("请输入需要批改 / 修改分数的测试：");
                    str = input.readLine();
                    if (isTestExist(str)) {
                        test = str;
                        System.out.println("[Opt0]查看学生作答并记录分数(自动跳过所有已记录分数的作答)  [Opt1]仅记录分数");
                        System.out.print("请选择批改方式(除较短的文字主观题外，建议选取 opt1)：");
                        str = input.readLine().toLowerCase();
                        if (str.equals("opt0")){
                            path = getStudentFileDirectory() + "/Test/" + test + "/Student/";
                            if ((new File (path)).exists() == false) {
                                System.out.println("该测试当前无人作答！");
                                break;
                            }//似乎没有这个选项了

                            String[] documents = (new File(path)).list();
                            int cnt = 0;
                            for (String document : documents) {
                                String arr[] = document.split("_", 2);//前一段是学号，后一段是它交的文件名字
                                student = getStudentById(Integer.parseInt(arr[0]));

                                cnt += 1;
                                if (getCell(test, student.getStudentId()) != null) continue;

                                System.out.println(">>>>>>>>>>>>>>" + student.getIdAndName() + ": " + arr[1] + "<<<<<<<<<<<<<<");
                                try (BufferedReader br = new BufferedReader(new FileReader(new File(path + document)))) {
                                    String line = null;
                                    while ((line = br.readLine()) != null) {
                                        System.out.println(line);
                                    }
                                }
                                System.out.println(">>>>>>>>>>>>>>" + student.getIdAndName() + ": " + arr[1] + "<<<<<<<<<<<<<<");

                                System.out.print ("请输入当前学生的分数(阿拉伯数字)：");
                                str = input.readLine();/*警告，这里还没来得及做数字检测*/

                                setCell(student.getStudentId(), test, str);
                                socket = getStudentSockets(student.getStudentId());
                                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                                socketOutput.println(JSON.toJSONString(new Message("Result", test + "_" + str)));
                            }
                            System.out.println("所有提交的作答已批改完成！(当前测试已提交作答 " + cnt + " 份)");
                        } else if (str.equals("opt1")) {
                            System.out.println("请依次输入学生的学号和分数(阿拉伯数字)，学号和分数之间由空格隔开" +
                                    "，支持批量插入，请不要有空行，输入完成时请在新的一行输入 \"end\"");


                            int cnt = 0, missed = 0;
                            for (;;) {
                                str = input.readLine();
                                if (str.equals("end")) break;

                                try {
                                    String arr[] = str.split("\\s+", 2);

                                    studentId = Integer.parseInt(arr[0]);
                                    if (isStudentExist(studentId) == false) {
                                        missed += 1;
                                        continue;
                                    }
                                    setCell(studentId, test, arr[1]);

                                    if (getStudentSockets(studentId) != null) {
                                        socket = getStudentSockets(studentId);
                                        PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                                        socketOutput.println(JSON.toJSONString(new Message("Result", test + "_" + arr[1])));
                                    }

                                    cnt += 1;
                                } catch (Exception e){
                                    System.out.println("输入非法！");
                                    break;
                                }
                            }
                            System.out.println("成绩已记录！本次一共记录了 " + cnt + " 份成绩，有 " + missed + " 份成绩无效");
                        } else {
                            System.out.println("操作无效，请重新操作");
                        }
                    } else {
                        System.out.println("测试不存在！");
                    }
                    break;
                case "opt11": //查看学生测试成绩
                    System.out.println("[Opt0]查看某一次测试成绩  [Opt1]查看某一学生的所有测试成绩");
                    str = input.readLine().toLowerCase();
                    if (str.equals("opt0")) {
                        if (printTestNameList() == false) break;
                        System.out.print("请输入需要查看的测试：");
                        str = input.readLine();
                        if (isTestExist(str)) {
                            List<String> id = getColumn("studentId");
                            List<String> mark = getColumn(str);
                            for (int i = 0; i < id.size(); ++i) {
                                System.out.println(
                                        getStudentById(Integer.parseInt(id.get(i))).getIdAndName()
                                                + ": " + mark.get(i));
                            }
                        } else {
                            System.out.println("测试不存在！");
                        }
                        //打印一列
                    } else if (str.equals("opt1")) {
                        //打印一行
                        System.out.print("请输入目标学生学号：");
                        try {
                            studentId = Integer.parseInt(input.readLine());
                        } catch (Exception e){
                            System.out.println("输入非法！");
                            break;
                        }
                        if (isStudentExist(studentId) == true) {
                            printStudentTestList(studentId);
                        } else {
                            System.out.println("该学生不存在！");
                        }
                    } else {
                        System.out.println("操作无效，请重新输入");
                    }
                    break;
                case "opt12": //删除学生
                    System.out.print("请输入学生学号：");
                    str = input.readLine();
                    try {
                        studentId = Integer.parseInt(str);
                    } catch (Exception e){
                        System.out.println("输入格式非法！");
                        break;
                    }
                    if (isStudentExist(studentId)) {
                        student = getStudentById(studentId);
                        if (getStudentSockets(studentId) != null) {
                            System.out.println("学生 " + student.getIdAndName() + "处于在线状态，删除会使他被强制掉线");
                            System.out.print("确认要删除他吗 [y/N]：");
                            str = input.readLine().toLowerCase();
                            if (str.equals("y")) {
                                PrintStream socketOutput = new PrintStream(getStudentSockets(studentId).getOutputStream());//学生输出
                                socketOutput.println(JSON.toJSONString(new Message("Bye", "")));
                            } else {
                                System.out.println("操作已取消");
                                break;
                            }
                        } else {
                            System.out.print("你确认要删除学生 " + student.getIdAndName() + "吗 [y/N]：");
                            str = input.readLine().toLowerCase();
                            if (str.equals("y") == false) {
                                System.out.println("操作已取消");
                                break;
                            }
                        }
                        deleteStudent(studentId);
                        System.out.println("学生已删除！");
                    } else {
                        System.out.println("学生不存在！");
                    }
                    break;
                case "opt13"://删除测试
                    if (printTestNameList() == false) break;
                    System.out.print("请输入需要删除的测试：");
                    str = input.readLine();
                    if (isTestExist(str)) {
                        deleteColumn(str);
                        if (getFileType(test) != null) setFileType(test, null);
                        System.out.println("测试已删除！");
                    } else {
                        System.out.println("测试不存在！");
                    }
                    break;
                default:
                    System.out.println("操作无效，请重新输入");
                    break;
            }
        }
    }
}