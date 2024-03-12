package Service;

import Interface.IOInterface;
import Interface.TaskInterface;
import jakarta.annotation.Resource;
import org.jdom2.Element;
import org.springframework.stereotype.Component;

import java.io.File;

import static Constant.Constant.DEV_CLASSPATH;

@Component
public class IOService implements IOInterface {

    @Resource
    private TaskInterface taskInterface;

    @Override
    public Element getRootElement(File nfoFile) {
        return null;
    }

    @Override
    public void changeClassPath(String classPath) {
        File file = new File(classPath);
        if (!file.exists()) {
            System.out.println("#   路径有误");
        } else {
            DEV_CLASSPATH = classPath;
            taskInterface.showTaskList();
        }
    }
}
