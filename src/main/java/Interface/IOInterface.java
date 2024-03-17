package Interface;

import Constant.NfoHelperResult;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@Lazy
public interface IOInterface {
    /**
     * 获取 .nfo 信息文件的根 Element 对象
     * @param nfoFile .nfo 文件
     * @return JDOM Element 对象
     */
    Element getRootElement(File nfoFile) throws IOException, JDOMException;

    /**
     * 修改工作路径
     */
    NfoHelperResult<String> changeClassPath(String path);

    /**
     * 拉出当前工作路径下所有文件夹内的文件夹至工作路径中
     * @return 操作结果
     */
    NfoHelperResult<String> pullFolderToClassPath() throws InterruptedException;

    /**
     * 获取文件夹路径内所有文件夹内的 .nfo 文件集合
     * @param dis 工作路径
     * @return .nfo 文件集合
     * @date  2024年3月14日22:08:23
     */
    NfoHelperResult<List<File>> listFoldersNfo(File dis);

    /**
     * 修改 .nfo 描述文件内女艺人的名字
     * @param actorName 目标名称
     * @return 操作结果
     * @date 2024年3月14日22:10:11
     */
    NfoHelperResult<String> changeActorName(String actorName, File nfoFile);

    /**
     * 当且仅当 .nfo 内缺失女艺人名字时，为其添加该名称标签
     * @param actorName 名称
     * @param nfoFile .nfo File 对象
     * @return 操作结果
     * @date 2024年3月16日01:10:50
     */
    NfoHelperResult<String> addActorNameIfAbsent(String actorName, File nfoFile);

    /**
     * 将 Element 对象覆盖写回 .nfo 文件
     * @param nfoFile .nfo File 对象
     * @param root Element 元素
     * @return 操作结果
     * @date 2024年3月16日01:36:43
     */
    NfoHelperResult<String> rewriteNfoFile(File nfoFile, Element root);

    /**
     * 为 .nfo 文件插入一条新 tag
     * @param nfoFile .nfo File 对象
     * @param tagName tag 名称
     * @return 操作结果
     * @date 2024年3月16日01:36:43
     */
    NfoHelperResult<String> addOneTag(File nfoFile, String tagName);

    /**
     * 根据下标删除 .nfo 文件的 tag 标签
     * @param nfoFile .nfo File 对象
     * @param index 下标
     * @return 操作结果
     * @date 2024年3月16日01:36:43
     */
    NfoHelperResult<String> deleteTagByIndex(File nfoFile, Integer index);

    /**
     * 显示该路径下的 .nfo 文件的所有标签
     * @param nfoFilePath .nfo 文件夹路径
     * @return 返回读取的 .nfo File 对象
     * @date 2024年3月17日08:45:37
     */
    NfoHelperResult<File> showAllTag(File nfoFilePath);
}
