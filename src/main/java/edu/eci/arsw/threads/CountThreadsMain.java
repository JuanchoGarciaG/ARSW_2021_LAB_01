/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

/**
 *
 * @author hcadavid
 */
public class CountThreadsMain {

    public static void main(String a[]){
        CountThread countThread1 = new CountThread(0,99);
        CountThread countThread2 = new CountThread(100,199);
        CountThread countThread3 = new CountThread(200,299);
        // Threads
        Thread thread1 = new Thread(countThread1);
        Thread thread2 = new Thread(countThread2);
        Thread thread3 = new Thread(countThread3);

        System.out.println("---------------");
        System.out.println("Run Execution");
        thread1.run();
        thread2.run();
        thread3.run();
        System.out.println("---------------");


        System.out.println("---------------");
        System.out.println("Start Execution");
        thread1.start();
        thread2.start();
        thread3.start();
        System.out.println("---------------");
    }
    
}
