package ru.vsu.amm.pp.Laba2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Александр on 04.02.2015.
 */
public class JavaThread {
    public static Random r = new Random();
    public static List<Integer> mass = new ArrayList<Integer>();
    public static final int N = 500;
    public static int sum = 0;

    public static class ThreadFunc extends Thread {
        private int sum;

        public int getSum() {
            return sum;
        }

        @Override
        public void run(){
            for (int i = N/2; i < N; i++) {
                sum+= mass.get(i);
            }
            System.out.println("Сумма побочного потока:\t" + this.sum);
        }
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        //Инициализация массива
        for (int i = 0; i < N; i++) {
            mass.add(r.nextInt(10));
        }
//        for (Integer mas : mass) {
//            sum += mas;
//        }
        //Создание и запуск побочного потока
        ThreadFunc javaThread = new ThreadFunc();
        javaThread.start();

        for ( int i=0; i < N/2;i++){
            sum+=mass.get(i);
        }
        System.out.println("Сумма главного потока:\t" + sum);
        sum+=javaThread.getSum();
        System.out.println("Сумма массива:\t" + sum);
        System.out.println("Главный поток завершен");

        System.out.println("Время выполнения = " + (System.currentTimeMillis()-startTime) + " мс");
    }
}
