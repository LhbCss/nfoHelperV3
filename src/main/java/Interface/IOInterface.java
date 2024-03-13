package Interface;

import Constant.NfoHelperResult;
import org.jdom2.Element;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
@Component
@Lazy
public interface IOInterface {
    /**
     * 获取 .nfo 信息文件的根 Element 对象
     * @param nfoFile .nfo 文件
     * @return JDOM Element 对象
     */
    Element getRootElement(File nfoFile);

    /**
     * 修改工作路径
     */
    NfoHelperResult<String> changeClassPath(String path);

    /**
     * 拉出当前工作路径下所有文件夹内的文件夹至工作路径中
     * @return 操作结果
     */
    NfoHelperResult<String> pullFolderToClassPath() throws InterruptedException;
}
