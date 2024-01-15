package com.chinamobile.upload2obs.action;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
@Slf4j
public class DeleteRecord {

    private final static DateTimeFormatter DATE10_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String AIRECOEDPATH = "/data/voice_file/voiking/ai/";

    public static void processWithMonitor() {
        log.info("开始删除{}天前的录音文件", 3);
        Date needDeleteDay = minusDays(new Date(), 3);
        deleteRecord(AIRECOEDPATH, needDeleteDay);
    }


    private static void deleteRecord(String recordPath, Date needDeleteDay) {
        File voikingFile = new File(recordPath);
        if (voikingFile.exists() && voikingFile.isDirectory()) {
            File[] files = voikingFile.listFiles();
            // REC202008
            for (File secFile : files) {
                if (secFile.isDirectory()) {
                    File[] thirdFiles = secFile.listFiles();
                    if (thirdFiles == null || thirdFiles.length == 0) {
                        // 删除没有文件的目录
                        secFile.delete();
                        continue;
                    }
                    // 20200817 ……
                    for (File thirdFile : thirdFiles) {
                        if (thirdFile.isDirectory()) {
                            if (date10(needDeleteDay).replace("-","").compareTo(thirdFile.getName()) > 0) {
                                // 删除文件夹内所有文件
                                for (File waitDelFile : thirdFile.listFiles()) {
                                    if (waitDelFile.isFile()) {
                                        log.info("删除文件：{}", waitDelFile.getName());
                                        waitDelFile.delete();
                                    }
                                }
                                thirdFile.delete();
                                log.info("删除目录：{}", thirdFile.getName());
                            }
                        }
                    }
                }
            }

        }
    }

    private static String date10(Date date) {
        return asLocalDateTIme(date).format(DATE10_FORMATTER);
    }

    private static LocalDateTime asLocalDateTIme(Date date) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        return date.toInstant().atZone(defaultZoneId).toLocalDateTime();
    }

    private static Date minusDays(Date date, int days) {
        return asDate(asLocalDateTIme(date).minusDays(days));
    }

    private static Date asDate(LocalDateTime localDateTime) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        return Date.from(localDateTime.atZone(defaultZoneId).toInstant());
    }
}
