package com.jni.more;

/**
 * join的用法，哪个线程调用join哪个线程就插队先执行
 */

public class JoinThread extends Thread {

    public static void main(String[] args) throws InterruptedException {
        //开启Father线程,满足Father需求
        new Thread(new Father()).start();
    }

}

class Father implements Runnable {

    @Override
    public void run() {
        System.out.println("老爸要抽烟,发现没烟了,给了100块让儿子去买中华......");
        Thread son = new Thread(new Son());   //让儿子去买烟
        son.start();    //开启儿子线程后，儿子线程进入就绪状态等待CPU调度，不一定立即执行儿子线程，所以可能会出现儿子没把烟买回来老爸就有烟抽了

//        try {
//            son.join(); //让儿子先插队去买烟
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            System.out.println("儿子不见了,报警寻找");
//        }

        System.out.println("老爸接过烟,把零钱给了儿子");
        System.out.println("儿子很开心,出门去了游戏厅");
    }
}

class Son implements Runnable {

    @Override
    public void run() {
        System.out.println("儿子接过钱蹦跶出了门");
        System.out.println("路过游戏厅玩了10秒钟");
        for (int i = 1; i <= 10; i++) {
            System.out.println(i + "秒");
            try {
                Thread.sleep(1000); //此时休眠可能会让其他线程进行,出错率增加,通过join方法解决
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("赶紧去买烟");
        System.out.println("回家把烟给老爸");
    }
}

