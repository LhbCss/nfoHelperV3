package Service;

import Constant.NfoHelperResult;
import Interface.IOInterface;
import Interface.TaskInterface;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
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
                + "#    Task 2 -> 将当前工作目录下的所有文件夹内的 .nfo 文件内艺人名字改为输入的值" + "\n"
                + "---------------------------------------------------" + "\n"
        );
    }

    @Override
    public void handleSelect() {
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.println("#   请输入 Task 代号以执行任务 -> ");
            try {
                System.in.reset();
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
                    case 2: {
                        sc = new Scanner(System.in);
                        String actorName = sc.nextLine();
                        NfoHelperResult<List<File>> result = ioInterface.listFoldersNfo(new File(DEV_CLASSPATH));
                        if (result.getSuccess() && !result.getData().isEmpty()) {
                            List<File> nfoFiles = result.getData();
                            for (File nfo : nfoFiles) {
                                NfoHelperResult<String> taskResult = ioInterface.changeActorName(actorName, nfo);
                                if (!taskResult.getSuccess()) {
                                    // 输出错误信息并结束
                                    System.out.println("#[WARN]    " + taskResult.getData());
                                    break;
                                }
                            }
                        }
                    }
                    default: {
                        System.out.println("#   输入的数字有误");
                        break;
                    }
                }
            } catch (InputMismatchException | IOException e) {
                System.out.println("#   请输入一个数字");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
