package Service;

import Constant.NfoHelperResult;
import Constant.UtilAid;
import Interface.IOInterface;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        for (File classPathFolder : classPathFolders) {
            UtilAid.infoConsole("获取到工作路径下的文件夹 -> " + classPathFolder.getName());
        }
        // 将每个文件夹分配到工作线程池完成操作
        int threadNum = classPathFolders.size();
        TASK_EXECUTOR = Executors.newFixedThreadPool(threadNum);
        UtilAid.infoConsole("开辟 " + threadNum + " 个线程并发执行拖拽任务");
        // 用于等待每个线程工作结束后返回
        latch = new CountDownLatch(threadNum);
        // 为线程池提交任务
        AtomicInteger index = new AtomicInteger();
        for (File folder : classPathFolders) {
            TASK_EXECUTOR.submit(() -> {
                // 工作路径下的每个文件夹，需要将它们里面的首个文件夹拉到工作路径下
                try {
                    handlePullFolder(folder, index.incrementAndGet());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        latch.await();
        // 关闭线程池
        TASK_EXECUTOR.shutdown();
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
            for (int i = 0; i < childFolders.size(); i++) {
                // 获取子文件夹中的所有文件
                List<File> files = Arrays.stream(Objects.requireNonNull(childFolders.get(i).listFiles())).toList();
                for (File file : files) {
                    if (file.getName().endsWith(NFO_SUFFIX)) {
                        UtilAid.infoConsole("发现 .nfo 文件 " + file.getName());
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
    public NfoHelperResult<String> addActorName(String actorName, File nfoFile) {
        if (!nfoFile.getName().endsWith(NFO_SUFFIX)) {
            return new NfoHelperResult<>(false, "File 对象不是一个 .nfo 文件");
        }
        Element root = getRootElement(nfoFile);
        List<Element> actorChildren = root.getChildren("actor");
        // 检查是否存在重复艺人名称
        for (Element actor : actorChildren) {
            Element name = actor.getChild("name");
            String text = name.getText();
            if (text.equalsIgnoreCase(actorName)) {
                return new NfoHelperResult<>(false, "名称重复");
            }
        }
        // 无差别添加一条名称标签并返回
        Element actor = new Element("actor");
        Element name = new Element("name");
        name.setText(actorName); // <name>actorName</name>
        actor.addContent(name); // <actor><name>actorName</name></actor>
        root.addContent(actor);
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
            fileWriter.write("");
            fileWriter.close();
            FileOutputStream fos = new FileOutputStream(nfoFile);
            fos.write(XML_HEADER.getBytes());
            // 使用 OutPutStream 将 文件写出
            XML_OUTPUTTER.output(root, fos);
            fos.close();
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
            tagChildren.remove(index - 1);
            rewriteNfoFile(nfoFile, root);
            return new NfoHelperResult<>("操作成功");
        }
        return new NfoHelperResult<>(false, "该 .nfo 标签不存在任何 tag 标签！");
    }

    @Override
    public NfoHelperResult<File> showAllTag(File nfoFilePath) {
        // 第一步 获取当前工作路径下所有 .nfo 文件对象
        List<File> files = Arrays.stream(Objects.requireNonNull(nfoFilePath.listFiles())).toList();
        List<File> nfoFileList = new ArrayList<>();
        for (File file : files) {
            if (!file.isDirectory() && file.getName().endsWith(NFO_SUFFIX)) {
                nfoFileList.add(file);
            }
        }
        if (!nfoFileList.isEmpty()) {
            File nfoFile = nfoFileList.get(0);
            UtilAid.infoConsole("当前读取的 .nfo 文件为：" + nfoFile.getName() + "，其标签为：");
            Element rootElement = getRootElement(nfoFile);
            List<Element> tagChildren = rootElement.getChildren("tag");
            if (!tagChildren.isEmpty()) {
                // 输出所有 tag
                for (int i = 0; i < tagChildren.size(); i++) {
                    UtilAid.infoConsole(i + 1 + ". <tag>" + tagChildren.get(i).getText() + "</tag>");
                }
                return new NfoHelperResult<>(true, nfoFile);
            }
            return new NfoHelperResult<>(true, nfoFile);
        }
        return new NfoHelperResult<>(false, "该路径下没有 .nfo 文件", (File) null);
    }

    private void handlePullFolder(File folder, Integer index) throws IOException {
        List<File> files = Arrays.stream(Objects.requireNonNull(folder.listFiles())).toList();
        if (!files.isEmpty()) {
            try {
                // 获取工作路径内文件夹内的文件夹 File 对象
                for (File file : files) {
                    if (file.isDirectory()) {
                        Path srcDir = file.toPath();
                        Path destDir = Paths.get(DEV_CLASSPATH, file.getName());
//                        if (!Files.exists(destDir)) {
//                            Files.createDirectories(destDir);
//                        }
                        Files.move(srcDir, destDir, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            } catch (IOException e) {
                UtilAid.warnConsole("拷贝文件时出现异常，异常信息：\n" + e);
            } finally {
                UtilAid.infoConsole("文件夹 " + folder.getName() + " 拖拽完毕，线程#" + index + " 关闭");
                latch.countDown();
            }
        }
    }

    public NfoHelperResult<String> matchThenChangeActorName(String featureName, String newName) throws IOException, JDOMException {
        // 第一步，获取所有文件夹的 .nfo 文件集合
        NfoHelperResult<List<File>> result = listFoldersNfo(new File(DEV_CLASSPATH));
        if (result.getSuccess()) {
            List<File> nfoFiles = result.getData();
            if (!nfoFiles.isEmpty()) {
                for (File nfo : nfoFiles) {
                    Element rootElement = getRootElement(nfo);
                    // 第二步，获取 <actor> 标签集合
                    List<Element> actorTags = rootElement.getChildren("actor");
                    for (Element actorTag : actorTags) {
                        // 第三步 检查是否匹配
                        String name = actorTag.getChild("name").getText();
                        if (name.contains(featureName) && !name.equals(newName)) {
                            UtilAid.infoConsole("发现匹配的艺人名：" + name);
                            actorTag.getChild("name").setText(newName);
                        }
                    }
                    // 第四步 写回
                    rewriteNfoFile(nfo, rootElement);
                }
            }
        } else {
            UtilAid.warnConsole(result.getInfo());
        }
        return new NfoHelperResult<>("ok");
    }
}
