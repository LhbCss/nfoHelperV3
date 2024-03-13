package Service;

import Constant.NfoHelperResult;
import Interface.IOInterface;
import Interface.TaskInterface;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import static Constant.Constant.DEV_CLASSPATH;

@Component
public class TaskService implements TaskInterface {

    @Resource
    private final IOInterface ioInterface;

    public TaskService(IOInterface ioInterface) {
        this.ioInterface = ioInterface;
    }

    @Override
    public void showTaskList() {
        System.out.println(
                "---------------------------------------------------" + "\n"
                + "#    当前执行目录：" + DEV_CLASSPATH + "\n"
                + "#    Task 0 -> 修改工作路径（慎重！工作路径出错可能造成无法恢复的后果）" + "\n"
                + "#    Task 1 -> 拉出当前工作路径下所有文件夹内的文件夹至工作路径中" + "\n"
                + "---------------------------------------------------" + "\n"
        );
    }

    @Override
    public void handleSelect() {
        Scanner sc = new Scanner(System.in);
        System.out.println("#   请输入 Task 代号以执行任务 -> ");
        try {
            int inputChoose = sc.nextInt();
            switch (inputChoose) {
                case 0: {
                    sc = new Scanner(System.in); // 因为上一次输入的换行符未被消费，所以这行需要消费掉换行符
                    System.out.println("#   请输入新的工作路径 ->");
                    String path = sc.nextLine();
                    NfoHelperResult<String> result = ioInterface.changeClassPath(path);
                    if (result.getSuccess()) {
                        showTaskList();
                    }
                    break;
                }
                case 1: {
                    NfoHelperResult<String> result = ioInterface.pullFolderToClassPath();

                    break;
                }
                default: {
                    System.out.println("#   输入的数字有误");
                    break;
                }
            }
            handleSelect();
        } catch (InputMismatchException e) {
            System.out.println("#   请输入一个数字");
            handleSelect();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
