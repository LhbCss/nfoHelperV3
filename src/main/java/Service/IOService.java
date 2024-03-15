package Service;

import Constant.NfoHelperResult;
import Interface.IOInterface;
import ch.qos.logback.core.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.awt.image.Kernel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static Constant.Constant.DEV_CLASSPATH;

@Component
@Lazy
public class IOService implements IOInterface {
    @Override
    public Element getRootElement(File nfoFile) {
        Document document = null;
        try {
            document = new SAXBuilder().build(nfoFile);
        } catch (IOException | JDOMException ioe) {
            System.out.println("#[WARN]    .nfo/XML 文件解析失败");
        }
        if (document != null) {
            return document.getRootElement();
        }
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

    private static final String NFO_SUFFIX = ".nfo";

    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n";

    private static final XMLOutputter XML_OUTPUTTER = new XMLOutputter(Format.getPrettyFormat());

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
        // 关闭线程池
        TASK_EXECUTOR.shutdown();
        System.out.println("#   并发任务结束，共移动了 " + threadNum + " 个文件夹");
        return new NfoHelperResult<>(true, "执行成功");
    }

    @Override
    public NfoHelperResult<List<File>> listFoldersNfo(File dis) {
        if (!dis.isDirectory()) {
            return new NfoHelperResult<>(false, (List<File>) null);
        }
        // 第一步 获取当前目录下所有文件夹
        List<File> childFolders = Arrays.stream(Objects.requireNonNull(dis.listFiles(File::isDirectory))).toList();
        // 第二步 遍历文件夹，获取 .nfo 文件
        List<File> nfoFileList = new ArrayList<>();
        if (!childFolders.isEmpty()) {
            for (int i = 0;i < childFolders.size();i++) {
                // 获取子文件夹中的所有文件
                List<File> files = Arrays.stream(Objects.requireNonNull(childFolders.get(i).listFiles())).toList();
                for (File file : files) {
                    if (file.getName().endsWith(NFO_SUFFIX)) {
                        nfoFileList.add(file);
                    }
                }
            }
        }
        return new NfoHelperResult<>(true, nfoFileList);
    }

    @Override
    public NfoHelperResult<String> changeActorName(String actorName, File nfoFile) {
        if (!nfoFile.getName().endsWith(NFO_SUFFIX)) {
            return new NfoHelperResult<>(false, "File 对象不是一个 .nfo 文件");
        }
        Element root = getRootElement(nfoFile);
        List<Element> children = root.getChildren();
        for (Element element : children) {
            if ("actor".equals(element.getName())) {
                List<Element> children1 = element.getChildren();
                for (Element element1 : children1) {
                    if ("name".equals(element1.getName())) {
                        element1.setText(actorName);
                    }
                }
            }
        }
        return rewriteNfoFile(nfoFile, root);
    }

    @Override
    public NfoHelperResult<String> addActorNameIfAbsent(String actorName, File nfoFile) {
        if (!nfoFile.getName().endsWith(NFO_SUFFIX)) {
            return new NfoHelperResult<>(false, "File 对象不是一个 .nfo 文件");
        }
        Element root = getRootElement(nfoFile);
        List<Element> actorChildren = root.getChildren("actor");
        if (actorChildren.isEmpty()) {
            // 不存在 <actor></actor> 标签，为其添加
            Element actor = new Element("actor");
            Element name = new Element("name");
            name.setText(actorName); // <name>actorName</name>
            actor.addContent(name); // <actor><name>actorName</name></actor>
            root.addContent(actor);
        }
        return rewriteNfoFile(nfoFile, root);
    }

    @Override
    public NfoHelperResult<String> rewriteNfoFile(File nfoFile, Element root) {
        try {
            // 清空原 movie.nfo 数据
            FileWriter fileWriter = new FileWriter(nfoFile);
            fileWriter.write(XML_HEADER);
            fileWriter.close();
            // 使用 OutPutStream 将 文件写出
            XML_OUTPUTTER.output(root, new FileOutputStream(nfoFile));
        } catch (IOException e) {
            System.out.println("#[WARN]    无法修改 " + nfoFile.getName() + "，文件已不存在或 .exe 没有删除该路径下文件的权限");
            return new NfoHelperResult<>(false, "无法修改/编辑 .nfo 文件");
        }
        return new NfoHelperResult<>(true, "修改成功");
    }

    @Override
    public NfoHelperResult<String> addOneTag(File nfoFile, String tagName) {
        if (!nfoFile.getName().endsWith(NFO_SUFFIX)) {
            return new NfoHelperResult<>(false, "File 对象不是一个 .nfo 文件");
        }
        Element root = getRootElement(nfoFile);
        Element tag = new Element("tag");
        tag.setText(tagName);
        root.addContent(tag);
        rewriteNfoFile(nfoFile, root);
        return new NfoHelperResult<>(true, "操作成功");
    }

    @Override
    public NfoHelperResult<String> deleteTagByIndex(File nfoFile, Integer index) {
        if (!nfoFile.getName().endsWith(NFO_SUFFIX)) {
            return new NfoHelperResult<>(false, "File 对象不是一个 .nfo 文件");
        }
        Element root = getRootElement(nfoFile);
        List<Element> tagChildren = root.getChildren("tag");
        if (!tagChildren.isEmpty()) {
            int size = tagChildren.size();
            if (index > size || index < 0) {
                return new NfoHelperResult<>(false, "输入的下标值不合法");
            }
            // 删除原有的所有 tag 标签
            root.removeChildren("tag");
            tagChildren.remove(index - 1);
            root.addContent(tagChildren);
            rewriteNfoFile(nfoFile, root);
            return new NfoHelperResult<>("操作成功");
        }
        return new NfoHelperResult<>(false, "该 .nfo 标签不存在任何 tag 标签！");
    }

    private void handlePullFolder(File folder) throws IOException {
        List<File> files = Arrays.stream(Objects.requireNonNull(folder.listFiles())).toList();
        if (!files.isEmpty()) {
            try {
                // 获取工作路径内文件夹内的文件夹 File 对象
                for (File file : files) {
                    FileUtils.copyDirectoryToDirectory(file, new File(DEV_CLASSPATH));
                    FileUtils.deleteDirectory(file);
                }
            } finally {
                latch.countDown();
            }
        }
    }
}
