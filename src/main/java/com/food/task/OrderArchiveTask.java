// 技术栈：Spring Task + MyBatis
package com.food.task;

import com.food.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Component
public class OrderArchiveTask {

    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点
    @Transactional
    public void archiveOrders() {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        orderMapper.archiveCompletedOrders(sixMonthsAgo);
        orderMapper.deleteArchivedOrders(sixMonthsAgo);
    }
}
