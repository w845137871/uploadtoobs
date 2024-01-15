package com.chinamobile.upload2obs.action;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

@Slf4j
public class DeleteFsStorage {

    private final static DateTimeFormatter DATE10_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    private static final String datetimeFormat = "yyyyMMdd";

    private static final String AIRECOEDPATH = "/data/voice_file/storage";

    volatile static LinkedHashSet set;

    private static void r() {
        try (Stream<Path> paths = Files.walk(Paths.get(AIRECOEDPATH), 2)) {
            set = new LinkedHashSet();
            paths.map(p -> p.toString()).filter(f -> f.endsWith(".wav")).forEach(e -> {
                try {
                    if (isDel(e)) {
                        set.add(e);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        } catch (IOException ex) {
            log.info(ex.getMessage(),ex);
        }

    }

    public static void processWithMonitor() {
        r();
        Iterator<String> iterator = set.iterator();
        while(iterator.hasNext()){
            String next = iterator.next();
            deleteRecord(next);
            iterator.remove();
        }
    }

    private static boolean isDel(String p) throws IOException {
        File file = new File(p);
        // 根据文件的绝对路径获取Path
        Path path = Paths.get(file.getAbsolutePath());
        // 根据path获取文件的基本属性类
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        // 从基本属性类中获取文件创建时间
        if (date10(minusDays(new Date(), 1)).replace("-","")
                .compareTo(formatDate(attrs.creationTime().toMillis())) > 0) {
            return true;
        }
        return false;

    }

    private static String formatDate(final long timestamp) {
        return new DateTime(timestamp).toString(datetimeFormat);
    }


    private static void deleteRecord(String recordPath) {
        File f = new File(recordPath);
        f.delete();
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
