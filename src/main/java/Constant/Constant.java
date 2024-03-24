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
        // E:\testV3\青年大学习\nested:\C:\Users\84623\AppData\Local\Temp\
        // e4jA4D.tmp_dir1710702181\nfoHelper.jar\!BOOT-INF\classes\!\nfohelperv3\dev
        int nestedIndex = path.indexOf("nested");
        if (nestedIndex != -1) {
            String newPath = path.substring(0, nestedIndex);
            File file = new File(newPath);
            DEV_CLASSPATH = file.getAbsolutePath();
        } else {
            DEV_CLASSPATH = DevApplication.class.getResource("").getPath();
        }
    }
}
