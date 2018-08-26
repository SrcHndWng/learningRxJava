package com.example.learningRxJava;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;
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

    private Flowable<String> crateFlowable() {
        return Flowable.create(
                new FlowableOnSubscribe<String>() {

                    @Override
                    public void subscribe(FlowableEmitter<String> emitter) throws Exception {

                        String[] datas = {"Hello, World!", "こんにちは、世界！"};

                        for (String data : datas) {
                            // 購読解除されている場合は処理をやめる
                            if (emitter.isCancelled()) {
                                return;
                            }

                            // データを通知する
                            emitter.onNext(data);
                        }

                        // 完了したことを通知する
                        emitter.onComplete();
                    }
                },
                BackpressureStrategy.BUFFER); // 超過したデータはバッファする
    }

    private ResourceSubscriber<String> createSubscriber() {
        return new ResourceSubscriber<String>() {
            /** 購読の開始時間 */
            private long startTime;

            // 購読が開始された際の処理
            @Override
            protected void onStart() {
                // 購読の開始時間を取得
                startTime = System.currentTimeMillis();
                // データ数のリクエスト
                request(Long.MAX_VALUE);
            }

            // データを受け取った際の処理
            @Override
            public void onNext(String data) {
                // 購読開始から500ミリ秒を過ぎた場合は購読を解除する
                if ((System.currentTimeMillis() - startTime) > 500L) {
                    dispose(); //  購読を解除する
                    System.out.println("購読解除しました");
                    return;
                }

                // 重い処理をしているとみなして1000ミリ秒待機
                // ※ 本来はThread.sleepは呼ぶべきではない
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }

                // 実行しているThread名の取得
                String threadName = Thread.currentThread().getName();
                // 受け取ったデータを出力する
                System.out.println(threadName + ": " + data);
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
        Flowable<String> flowable = crateFlowable();

        // Subscriberを生成する
        ResourceSubscriber<String> subscriber = createSubscriber();

        flowable
                // Subscriberの処理を別スレッドで行うようにする
                .observeOn(Schedulers.computation())
                // 購読する
                .subscribe(subscriber);

        // しばらく待つ
        Thread.sleep(1500L);
    }
}
