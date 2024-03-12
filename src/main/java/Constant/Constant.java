package Constant;

import nfohelperv3.dev.DevApplication;

import java.io.File;

/**
 * 常量类
 * @author Linhebin
 * @since 2024/3/12
 */
public class Constant {
    /**
     * 启动类绝对路径、工作路径
     * 格式：E:\ideaProject\dev\target\classes\nfohelperv3\dev
     */
    public static String DEV_CLASSPATH;

    static {
        String path = DevApplication.class.getResource("").getPath();
        File file = new File(path);
        DEV_CLASSPATH = file.getAbsolutePath();
    }
}
