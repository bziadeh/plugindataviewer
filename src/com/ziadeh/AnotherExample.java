package com.ziadeh;

public class AnotherExample {

    private long created = System.currentTimeMillis();
    private int timeElapsed = 0;

    public AnotherExample() {
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timeElapsed++;
                }
            }
        }.start();
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public String getName() {
        return "A Cool Object";
    }
}
