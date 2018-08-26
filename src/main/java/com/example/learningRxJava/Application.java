package com.example.learningRxJava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import rx.Observable;

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
        // あいさつの言葉を通知するObservableの生成
        Observable.just("Hello, World!", "こんにちは、世界！")
                // Observableを購読し処理を開始する
                .subscribe(
                        // Observableからのデータを受け取った際の処理
                        item -> {
                            // 実行しているThread名の取得
                            String threadName = Thread.currentThread().getName();
                            // Observableからのデータをそのまま標準出力する
                            System.out.println(threadName + ": " + item);
                        },
                        // Observableからエラーを通知された際の処理
                        error -> {
                            error.printStackTrace();
                        },
                        // Observableから完了を通知された際の処理
                        () -> {
                            // 実行しているThread名の取得
                            String threadName = Thread.currentThread().getName();
                            System.out.println(threadName + ": 完了しました");
                        });
    }
}
