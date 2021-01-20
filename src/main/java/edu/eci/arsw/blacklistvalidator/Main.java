/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.List;

/**
 *
 * @author hcadavid
 */
public class Main {
    


    public static void main(String[] a){
        int processors = getProcessors(), threads = 500;
        long start = System.currentTimeMillis();
        HostBlackListsValidator hblv=new HostBlackListsValidator();
        List<Integer> blackListOcurrences=hblv.checkHost("202.24.34.55", threads);
        long end = System.currentTimeMillis();
        System.out.println("The host was found in the following blacklists:"+blackListOcurrences);
        System.out.println("#Threads: "+threads+", Elapsed time: "+(end-start)+" ms");

    }
    private static int getProcessors(){
        Runtime runtime = Runtime.getRuntime();
        int processors = runtime.availableProcessors();
        return processors;
    }
    
}
