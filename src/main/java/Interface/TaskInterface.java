package Interface;

import Constant.NfoHelperResult;
import org.springframework.stereotype.Component;

@Component
public interface TaskInterface {
    /**
     * 展示任务清单
     */
    void showTaskList();

    /**
     * 执行选择任务
     */
    void handleSelect();
}
