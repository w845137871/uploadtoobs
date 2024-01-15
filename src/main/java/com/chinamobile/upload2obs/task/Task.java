package com.chinamobile.upload2obs.task;

import com.chinamobile.upload2obs.action.DeleteRecord;
import com.chinamobile.upload2obs.action.DeleteTTSRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

@Component
@Slf4j
public class Task {

    @Scheduled(cron = "0 20 23 * * ?")
    public void task() throws InterruptedException {
        MyRecursiveAction action = new MyRecursiveAction();
        action.run();
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void deleteRecordTask() {
        DeleteRecord.processWithMonitor();
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void deleteTTSRecordTask() {
        DeleteTTSRecord.processWithMonitor();
    }

}
