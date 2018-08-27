package com.example.learningRxJava;

import io.reactivex.Flowable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        try (ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args)) {
            Application app = ctx.getBean(Application.class);
            app.run();
        } catch (Exception e) {
            System.out.println("Application Error!");
            e.printStackTrace();
        }
    }

    private void run() throws Exception {
        // 「分:秒.ミリ秒」の文字列に変換するFormatter
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss.SSS");

        // 処理を開始する前の時間
        System.out.println("開始時間: " + LocalTime.now().format(formatter));

        // 1000ミリ秒後に数値「0」を通知するFlowableの生成
        Flowable<Long> flowable = Flowable.timer(1000L, TimeUnit.MILLISECONDS); // ①

        // 購読開始
        flowable.subscribe(
                // 第1引数： データの通知時
                data -> { // ②
                    // Thread名の取得
                    String threadName = Thread.currentThread().getName();
                    // 現在時刻の「分:秒.ミリ秒」を取得
                    String time = LocalTime.now().format(formatter);
                    // 出力
                    System.out.println(threadName + ": " + time + ": data=" + data);
                },
                // 第2引数： エラーの通知時
                error -> System.out.println("エラー=" + error),
                // 第3引数： 完了の通知時
                () -> System.out.println("完了")); // ③

        // しばらく待つ
        Thread.sleep(1500L);
    }
}
