package com.chinamobile.upload2obs;

import com.chinamobile.upload2obs.task.Task;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@SpringBootApplication
@EnableScheduling
@RestController
public class Upload2obsApplication {

    public static void main(String[] args) {
        SpringApplication.run(Upload2obsApplication.class, args);
    }

    @Resource
    private Task task;

    @RequestMapping("/task")
    public void test() throws InterruptedException {
        task.task();
    }

    @RequestMapping("/task1")
    public void test1() {
        task.deleteRecordTask();
    }

    @RequestMapping("/task2")
    public void test2() {
        task.deleteTTSRecordTask();
    }
}
