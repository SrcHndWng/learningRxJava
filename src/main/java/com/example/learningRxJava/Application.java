package com.example.learningRxJava;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import io.reactivex.subscribers.ResourceSubscriber;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.Subscriber;
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

    private Flowable<Long> crateFlowable() {
        // 100ミリ秒ごとに0から始まるデータを通知するFlowableを生成……（1）
        return Flowable.interval(100L, TimeUnit.MILLISECONDS)
                // BackpressureMode.DROPを設定した時と同じ挙動にする……（2）
                .onBackpressureDrop();
    }

    private DisposableSubscriber<Long> createSubscriber() {
        return new DisposableSubscriber<Long>() {
            // データを受け取った際の処理
            @Override
            public void onNext(Long item) {
                // 300ミリ秒待つ……（4）
                try {
                    Thread.sleep(300L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // 異常終了で終わる
                    System.exit(1);
                }

                // 実行しているThread名の取得
                String threadName = Thread.currentThread().getName();
                // 受け取ったデータを出力する
                System.out.println(threadName + ": " + item);
            }

            // 完了を通知された際の処理
            @Override
            public void onComplete() {
                // 実行しているスレッド名の取得
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + ": 完了しました");
            }

            // エラーを通知された際の処理
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        };
    }

    private void run() throws Exception {
        // あいさつの言葉を通知するFlowableの生成
        Flowable<Long> flowable = crateFlowable();

        // Subscriberを生成する
        Subscriber<Long> subscriber = createSubscriber();

        flowable
                // 非同期でデータを受け取るようにし、バッファサイズを2にする……（3）
                .observeOn(Schedulers.computation(), false, 2)
                // 購読する
                .subscribe(subscriber);

        // しばらく待つ
        Thread.sleep(2000L);
    }
}
