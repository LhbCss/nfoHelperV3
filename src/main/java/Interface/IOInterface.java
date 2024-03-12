package Interface;

import org.jdom2.Element;
import org.springframework.stereotype.Component;

import java.io.File;
@Component
public interface IOInterface {
    /**
     * 获取 .nfo 信息文件的根 Element 对象
     * @param nfoFile .nfo 文件
     * @return JDOM Element 对象
     */
    Element getRootElement(File nfoFile);

    /**
     * 修改工作路径
     * @param classPath 希望修改的工作路径
     */
    void changeClassPath(String classPath);
}
