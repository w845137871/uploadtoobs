package com.chinamobile.upload2obs.task;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
public class MyRecursiveAction extends RecursiveAction {


    private static final int NCPUS = Runtime.getRuntime().availableProcessors();

    private static final int THRESHOLD = (NCPUS > 1) ? NCPUS << 1 : 1;


    static LinkedBlockingQueue<String> linkedBlockingQueue;

    private static final String path = "/data/voice_file/voiking/ai/";
    private static final String datetimeFormat = "yyyyMMdd";

    private String formatDate(final long timestamp) {
        return new DateTime(timestamp).toString(datetimeFormat);
    }

    private void r() {

        StringBuilder recordPath = new StringBuilder();
        recordPath.append(path).append("REC");

        String ymd = formatDate(System.currentTimeMillis());
        String ym =  ymd.substring(0,6);
        recordPath.append(ym).append("/").append(ymd);

        try (Stream<Path> paths = Files.walk(Paths.get(recordPath.toString()), 2)) {
//        try (Stream<Path> paths = Files.walk(Paths.get("/data/voice_file/voiking/ai/REC202306"), 2)) {
            linkedBlockingQueue = new LinkedBlockingQueue<>();
            paths.map(p -> p.toString()).filter(f -> f.endsWith(".wav")).forEach(e -> {
                try {
                    linkedBlockingQueue.put(e);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            });
        } catch (IOException ex) {
            log.info(ex.getMessage(),ex);
        }

    }

    public void run() throws InterruptedException {
        r();
        ForkJoinPool pool = new ForkJoinPool();
        pool.submit(new MyRecursiveAction(1, linkedBlockingQueue.size()));
        pool.awaitTermination(2, TimeUnit.SECONDS);
        pool.shutdown();
    }



    private int s, e;

    public MyRecursiveAction(){
    }

    private MyRecursiveAction(int s, int e) {
        super();
        this.s = s;
        this.e = e;
    }

    @Override
    protected void compute() {
        if (e - s < THRESHOLD) {
            for (int i = s; i <= e; i++) {

                InputStream inputStream;
                String n;
                try {
                    n = linkedBlockingQueue.take();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    inputStream = new FileInputStream(n);
                    System.out.println(1111);
                    ObsConnection.uploadingFile("ai-record", removefirstChar(n), inputStream);
                } catch (FileNotFoundException e) {
                    log.error(e.getMessage(), e);
                } catch (Exception e) {
                    log.info(e.getMessage(), e);
                }
            }
        } else {
            int m = (s + e) / 2;
            MyRecursiveAction f = new MyRecursiveAction(s, m);
            MyRecursiveAction ts = new MyRecursiveAction(m + 1, e);
            invokeAll(f, ts);
        }
    }

    public static String removefirstChar(String str)
    {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(1);
    }

    static final class Node {
        volatile int s;
        volatile int e;
    }
}
