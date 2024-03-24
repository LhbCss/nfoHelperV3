package Service;

import Constant.NfoHelperResult;
import Constant.UtilAid;
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
                + "#    Task 0 -> 修改工作路径（慎重！工作路径出错可能造成无法恢复的后果）" + "\n"
                + "#    Task 1 -> 拉出当前工作路径下所有文件夹内的文件夹至工作路径中" + "\n"
                + "#    Task 2 -> 将当前工作目录下的所有文件夹内的 .nfo 文件内艺人名字改为输入的值" + "\n"
                + "#    Task 3 -> Task 1 + 2 同时执行（统一拉到工作目录改名字）" + "\n"
                + "#    Task 4 -> 将当前工作目录的所有文件夹内的 .nfo 文件添加女艺人的名字（仅修改名称未知的 .nfo 文件）" + "\n"
                + "#    Task 5 -> 为当前工作目录的所有文件夹内的 .nfo 文件添加一条标签" + "\n"
                + "#    Task 6 -> 微调 .nfo 文件标签 - 为当前工作目录下的 .nfo 文件删除一条指定标签（确保该路径下只有一个 .nfo 文件，否则只会取第一个修改）" + "\n"
                + "#    Task 7 -> 微调 .nfo 文件标签 - 为当前工作目录下的 .nfo 文件添加一条指定标签" + "\n"
                + "#    Task 8 -> 退出程序" + "\n"
                + "---------------------------------------------------" + "\n"
        );
    }

    @Override
    public void handleSelect() {
        while (true) {
            showClassPath();
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
                        long start = System.currentTimeMillis();
                        NfoHelperResult<String> result = ioInterface.pullFolderToClassPath();
                        long end = System.currentTimeMillis();
                        UtilAid.infoConsole("任务结束，共计耗时：" + (end - start) + " ms");
                        break;
                    }
                    case 2: {
                        sc = new Scanner(System.in);
                        System.out.println("#   请输入女艺人名字 -> ");
                        String actorName = sc.nextLine();
                        long start = System.currentTimeMillis();
                        NfoHelperResult<List<File>> result = ioInterface.listFoldersNfo(new File(DEV_CLASSPATH));
                        if (result.getSuccess() && !result.getData().isEmpty()) {
                            List<File> nfoFiles = result.getData();
                            for (File nfo : nfoFiles) {
                                NfoHelperResult<String> taskResult = ioInterface.changeActorName(actorName, nfo);
                                if (!taskResult.getSuccess()) {
                                    // 输出错误信息并结束
                                    UtilAid.warnConsole(taskResult.getData());
                                    break;
                                }
                            }
                        }
                        long end = System.currentTimeMillis();
                        UtilAid.infoConsole("任务结束，共计耗时：" + (end - start) + " ms");
                        break;
                    }
                    case 3: {
                        sc = new Scanner(System.in);
                        System.out.println("#   请输入女艺人名字 -> ");
                        String actorName = sc.nextLine();
                        long start = System.currentTimeMillis();
                        NfoHelperResult<String> result = ioInterface.pullFolderToClassPath();
                        if (result.getSuccess()) {
                            NfoHelperResult<List<File>> result2 = ioInterface.listFoldersNfo(new File(DEV_CLASSPATH));
                            if (result.getSuccess() && !result.getData().isEmpty()) {
                                List<File> nfoFiles = result2.getData();
                                for (File nfo : nfoFiles) {
                                    NfoHelperResult<String> taskResult = ioInterface.changeActorName(actorName, nfo);
                                    if (!taskResult.getSuccess()) {
                                        // 输出错误信息并结束
                                        UtilAid.warnConsole(taskResult.getData());
                                        break;
                                    }
                                }
                                long end = System.currentTimeMillis();
                                UtilAid.infoConsole("任务结束，共计耗时：" + (end - start) + " ms");
                                break;
                            }
                            break;
                        }
                        break;
                    }
                    case 4: {
                        sc = new Scanner(System.in);
                        System.out.println("#   请输入女艺人名字 -> ");
                        String actorName = sc.nextLine();
                        long start = System.currentTimeMillis();
                        NfoHelperResult<List<File>> result = ioInterface.listFoldersNfo(new File(DEV_CLASSPATH));
                        if (result.getSuccess() && !result.getData().isEmpty()) {
                            for (File nfoFile : result.getData()) {
                                ioInterface.addActorNameIfAbsent(actorName, nfoFile);
                            }
                        }
                        long end = System.currentTimeMillis();
                        UtilAid.infoConsole("任务结束，共计耗时：" + (end - start) + " ms");
                        break;
                    }
                    case 5: {
                        sc = new Scanner(System.in);
                        System.out.println("#   请输入标签名称 -> ");
                        String tagName = sc.nextLine();
                        long start = System.currentTimeMillis();
                        NfoHelperResult<List<File>> result = ioInterface.listFoldersNfo(new File(DEV_CLASSPATH));
                        if (result.getSuccess() && !result.getData().isEmpty()) {
                            for (File nfoFile : result.getData()) {
                                ioInterface.addOneTag(nfoFile, tagName);
                            }
                        }
                        long end = System.currentTimeMillis();
                        UtilAid.infoConsole("任务结束，共计耗时：" + (end - start) + " ms");
                        break;
                    }
                    case 6: {
                        NfoHelperResult<File> ret = ioInterface.showAllTag(new File(DEV_CLASSPATH));
                        if (!ret.getSuccess()) {
                            UtilAid.warnConsole(ret.getInfo());
                            break;
                        }
                        sc = new Scanner(System.in);
                        System.out.println("#   请输入要删除的标签的索引 ->");
                        int delIndex = sc.nextInt();
                        long start = System.currentTimeMillis();
                        NfoHelperResult<String> result = ioInterface.deleteTagByIndex(ret.getData(), delIndex);
                        if (result.getSuccess()) {
                            UtilAid.infoConsole(result.getData());
                        } else {
                            UtilAid.warnConsole(result.getData());
                        }
                        long end = System.currentTimeMillis();
                        UtilAid.infoConsole("任务结束，共计耗时：" + (end - start) + " ms");
                        break;
                    }
                    case 7: {
                        NfoHelperResult<File> ret = ioInterface.showAllTag(new File(DEV_CLASSPATH));
                        if (!ret.getSuccess()) {
                            UtilAid.warnConsole(ret.getInfo());
                            break;
                        }
                        File nfoFile = ret.getData();
                        sc = new Scanner(System.in);
                        System.out.println("#   请输入希望添加的 tag 名称 ->");
                        String tagName = sc.nextLine();
                        long start = System.currentTimeMillis();
                        NfoHelperResult<String> result = ioInterface.addOneTag(nfoFile, tagName);
                        if (result.getSuccess()) {
                            UtilAid.infoConsole(result.getData());
                        } else {
                            UtilAid.warnConsole(result.getData());
                        }
                        long end = System.currentTimeMillis();
                        UtilAid.infoConsole("任务结束，共计耗时：" + (end - start) + " ms");
                        break;
                    }
                    case 8: {
                        System.exit(0);
                    }
                    default: {
                        UtilAid.warnConsole("输入的数字有误");
                        break;
                    }
                }
            } catch (InputMismatchException e) {
                UtilAid.warnConsole("请输入一个数字");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void showClassPath() {
        System.out.println(
                "---------------------------------------------------" + "\n" +
                "#  当前工作路径：" + DEV_CLASSPATH + "\n" +
                "---------------------------------------------------");
    }
}
