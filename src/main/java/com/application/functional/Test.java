package com.application.functional;

public class Test{
    public static void main(String[] args) {
        Thread t1 = new Thread(new Runnable(){
            @Override
            public void run() {
                System.out.println("使用匿名类方式创建线程");
            }
        });

        Thread t2 = new Thread( () -> System.out.println("使用lambda方式创建线程"));
        t1.start();
        t2.start();

    }
}
