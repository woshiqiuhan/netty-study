package demo01;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // 创建线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                3,
                5,
                5, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5));

        // 向线程池提交任务
        for (int i = 0; i < 7; i++) {
            int finalI = i;
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    for (int x = 0; x < 2; x++) {
                        System.out.println(Thread.currentThread().getName() + "任务 " + finalI + ":" + x);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        // 关闭线程池
        threadPoolExecutor.shutdown();
    }
}
