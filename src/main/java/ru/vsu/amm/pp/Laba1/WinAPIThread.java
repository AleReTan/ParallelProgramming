package ru.vsu.amm.pp.Laba1;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.W32APIOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class WinAPIThread {

    public static Random r = new Random();
    public static List<Integer> mass = new ArrayList<Integer>();
    public static final int N = 500;
    public static int sum = 0;

    public interface MoreKernel32 extends Kernel32 {
        static final MoreKernel32 instance = (MoreKernel32) Native.loadLibrary("kernel32", MoreKernel32.class, W32APIOptions.DEFAULT_OPTIONS);

        WinDef.DWORD ResumeThread(WinNT.HANDLE hThread);

        WinNT.HANDLE CreateThread(Pointer lpThreadAttributes, Pointer dwStackSize, Callback lpStartAddress, Pointer lpParameter, WinDef.DWORD dwCreationFlags, Pointer lpThreadId);

    }

    public static class ThreadFunc implements Callback {
        private int sum = 0;

        public void callback() {
            for (int i = N/2; i < N; i++) {
                sum += mass.get(i);
                //System.out.print("2");
            }

            System.out.println("Побочный поток завершен");
        }

        public int getSum() {
            return sum;
        }
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        //Инициализация массива
        for (int i = 0; i < N; i++) {
            mass.add(r.nextInt(10));
        }

//       for (Integer mas : mass) {
//            sum += mas;
//        }
        //Создание потока
        WinDef.DWORD flag = new WinDef.DWORD(4L);
        ThreadFunc func = new ThreadFunc();
        WinNT.HANDLE hThread = MoreKernel32.instance.CreateThread(null, null, func, null, flag, null);
        if (hThread == null) {
            System.out.println("Поток не создан");
            return;
        }

        MoreKernel32.instance.ResumeThread(hThread);

        for (int i = 0; i < N/2; i++) {
            sum+= mass.get(i);
            //System.out.print("1");
        }

        System.out.println("Сумма главного потока:\t" + sum);

        MoreKernel32.instance.WaitForSingleObject(hThread, -1);
        MoreKernel32.instance.CloseHandle(hThread);

        System.out.println("Сумма побочного потока:\t" + func.getSum());
        sum+=func.getSum();
        System.out.println("Сумма массива:\t" + sum);
        System.out.println("Главный поток завершен");

        System.out.println("Время выполнения = " + (System.currentTimeMillis()-startTime) + " мс");

    }
}
