package com.ziadeh;

public class ExampleObject {

    private int secondsExisted = 0;
    private AnotherExample anotherExample = new AnotherExample();

    public ExampleObject() {
        new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                secondsExisted++;
            }
        }).start();
    }

    public int getSecondsExisted() {
        return secondsExisted;
    }

    @Override
    public String toString() {
        return "Example Object";
    }
}
