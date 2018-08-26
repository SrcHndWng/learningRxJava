package com.example.learningRxJava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

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
                // 通知後の処理を非同期で行うためのSchedulerを設定
                .observeOn(Schedulers.computation()) // ・・・（1）
                // Observableを購読し処理を開始する
                .subscribe(
                        new Observer<String>() {

                            // Observableからのデータを受け取った際の処理
                            @Override
                            public void onNext(String item) {
                                // 実行しているThread名の取得
                                String threadName = Thread.currentThread().getName();
                                // Observableからのデータをそのまま標準出力する
                                System.out.println(threadName + ": " + item);
                            }

                            // Observableから完了を通知された際の処理
                            @Override
                            public void onCompleted() {
                                // 実行しているThread名の取得
                                String threadName = Thread.currentThread().getName();
                                System.out.println(threadName + ": 完了しました");
                            }

                            // Observableからエラーを通知された際の処理
                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }
                        });

        // 非同期で行われていることを確認するため出力する
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": subscribed!");

        // しばらく待つ
        Thread.sleep(5000L); // ・・・（2）

        // 終了メッセージ
        System.out.println(threadName + ": end!");        
    }
}
