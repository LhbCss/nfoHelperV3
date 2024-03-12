package Service;

import Interface.IOInterface;
import Interface.TaskInterface;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Scanner;

import static Constant.Constant.DEV_CLASSPATH;

@Component
public class TaskService implements TaskInterface {

    @Autowired
    private IOInterface ioInterface;

    {
        showTaskList();
        handleSelect();
    }
    @Override
    public void showTaskList() {
        System.out.println(
                "---------------------------------------------------" + "\n"
                + "#    当前执行目录：" + DEV_CLASSPATH + "\n"
                + "#    Task 1 -> 修改工作路径（慎重！工作路径出错可能造成无法恢复的后果）" + "\n"
                + "---------------------------------------------------" + "\n"
        );
    }

    @Override
    public void handleSelect() {
        Scanner sc = new Scanner(System.in);
        System.out.println("#   请输入 Task 代号以执行任务 -> ");
        int inputChoose = sc.nextInt();
        switch (inputChoose) {
            case 1: {
                sc.reset();
                String path = sc.nextLine();
                ioInterface.changeClassPath(path);
                break;
            }
            default: {
                System.out.println("#   输入的数字有误");
                break;
            }
        }
        handleSelect();
    }
}
