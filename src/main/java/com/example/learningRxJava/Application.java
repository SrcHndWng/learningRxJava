package com.example.learningRxJava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import rx.Observable;
import rx.Observer;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

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
        Observable<String> observableGreeting =
            Observable.create(new OnSubscribe<String>() {
                
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    // 購読解除されている場合は処理をやめる
                    if (subscriber.isUnsubscribed()) {
                        return;
                    }
                    
                    // 1回目の通知をする
                    subscriber.onNext("Hello, World!");
                    // 2回目の通知をする
                    subscriber.onNext("こんにちは、世界！");
                    
                    // 購読解除されていない場合
                    if (!subscriber.isUnsubscribed()) {
                        // 完了したことを通知する
                        subscriber.onCompleted();
                    }
                }
            });
            
        // Observableを購読し処理を開始する
        observableGreeting.subscribe(new Observer<String>() {
            
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
    }
}
