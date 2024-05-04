package nfohelperv3.dev;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.Random;

@SpringBootTest
class DevApplicationTests {

    public static final int[] ints = new int[999999999];

    public static int cnt = 0;

    static {
        for (int i = 0; i < 999999998; i++) {
            ints[i] = i + 1;
        }
    }

    void findRandomInt(int randomInt) {
        for (int i : ints) {
            cnt++;
            if (ints[i] == randomInt) return;
        }
    }

    void findRandomIntBinary(int randomInt) {
        int start = 0;
        int end = 999999998;
        while (end > start) {
            cnt++;
            int mid = (end + start) / 2;
            if (ints[mid] > randomInt) {
                end = mid;
            } else if (ints[mid] < randomInt) {
                start = mid;
            } else {
                return;
            }
        }
    }

    @Test
    void contextLoads() {
        Random r = new Random();
        int randomInt = Math.abs(r.nextInt() % 999999999 + 1);
        long start = System.currentTimeMillis();
        findRandomIntBinary(randomInt);
        long end = System.currentTimeMillis();
        System.out.println("随机数：" + randomInt);
        System.out.println("二分查找：" + cnt + "次");
        System.out.println("二分查找：" + (end - start) + "ms");
        cnt = 0;
        start = System.currentTimeMillis();
        findRandomInt(randomInt);
        end = System.currentTimeMillis();
        System.out.println("线性查找：" + cnt + "次");
        System.out.println("线性查找：" + (end - start) + "ms");
    }

}
