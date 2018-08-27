package com.example.learningRxJava;

import io.reactivex.Flowable;
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
        Flowable<Integer> result =
                Flowable
                        // 引数のデータを通知するFlowableを生成
                        .just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                        // 偶数のデータのみを通知する
                        .filter(data -> data % 2 == 0)
                        // 通知するデータを10倍にする
                        .map(data -> data * 10);

        // 購読する
        result.subscribe(data -> System.out.println("received = " + data));
    }
}
