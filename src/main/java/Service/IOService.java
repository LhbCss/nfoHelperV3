package Service;

import Constant.NfoHelperResult;
import Interface.IOInterface;
import Interface.TaskInterface;
import jakarta.annotation.Resource;
import org.jdom2.Element;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static Constant.Constant.DEV_CLASSPATH;

@Component
@Lazy
public class IOService implements IOInterface {
    @Override
    public Element getRootElement(File nfoFile) {
        return null;
    }

    /**
     * IO 业务类唯一工作线程池
     */
    static ExecutorService TASK_EXECUTOR;

    /**
     * 线程池同步计数器
     */
    static CountDownLatch latch;

    @Override
    public NfoHelperResult<String> changeClassPath(String path) {
        File file = new File(path.trim());
        if (!file.isDirectory()) {
            System.out.println("#   工作路径错误，不能是具体的文件路径，而是文件夹内路径");
            return new NfoHelperResult<>(false, "路径错误");
        } else {
            DEV_CLASSPATH = path;
            System.out.println("\n#   修改成功！\n");
            return new NfoHelperResult<>(true, "修改成功");
        }
    }

    @Override
    public NfoHelperResult<String> pullFolderToClassPath() throws InterruptedException {
        File classPath = new File(DEV_CLASSPATH);
        // 工作路径下的所有文件夹
        List<File> classPathFolders = Arrays.stream(Objects.requireNonNull(classPath.listFiles(File::isDirectory))).toList();
        for (int i = 0;i < classPathFolders.size();i++) {
            System.out.println("#" + (i + 1) + ".   获取到工作路径下的文件夹 -> " + classPathFolders.get(i).getName());
        }
        // 将每个文件夹分配到工作线程池完成操作
        int threadNum = classPathFolders.size();
        TASK_EXECUTOR = Executors.newFixedThreadPool(threadNum);
        // 用于等待每个线程工作结束后返回
        latch = new CountDownLatch(threadNum);
        // 为线程池提交任务
        for (File folder : classPathFolders) {
            TASK_EXECUTOR.submit(() -> {
                // 工作路径下的每个文件夹，需要将它们里面的首个文件夹拉到工作路径下
                try {
                    handlePullFolder(folder);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        latch.await();
        System.out.println("1");
        // 关闭线程池
        TASK_EXECUTOR.shutdown();
        return null;
    }

    private void handlePullFolder(File folder) throws IOException {
        File[] files = folder.listFiles();
        if (files != null) {
            // 获取工作路径内文件夹内的文件夹 File 对象
            File file = files[0];
            System.out.println("#   获取到工作路径内文件夹内文件 -> " + file + " ，尝试开始移动至工作路径...");
            try {
                FileCopyUtils.copy(file, new File(DEV_CLASSPATH));
            } finally {
                latch.countDown();
            }
        }
    }
}
