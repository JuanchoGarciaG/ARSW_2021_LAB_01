package edu.eci.arsw.math;

import edu.eci.arsw.blacklistvalidator.HostBlackListsValidator;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class HostBlackListsValidatorTest {

    @Test
    public void shouldCheckInShortTime(){
        HostBlackListsValidator hblv=new HostBlackListsValidator();
        long ini80threads = System.currentTimeMillis();
        hblv.checkHost("202.24.34.55", 80);
        long time80threads = System.currentTimeMillis() - ini80threads;
        long ini200threads = System.currentTimeMillis();
        hblv.checkHost("202.24.34.55", 200);
        long time200threads = System.currentTimeMillis() - ini200threads;
        Assert.assertTrue("Tiempo de 80 Threads debe ser mayor a 200 threads", time80threads>time200threads);
    }

    @Test
    public void shouldNotCheckInShortTime(){
        HostBlackListsValidator hblv=new HostBlackListsValidator();
        long ini80threads = System.currentTimeMillis();
        hblv.checkHost("202.24.34.55", 80);
        long time80threads = System.currentTimeMillis() - ini80threads;
        long ini200threads = System.currentTimeMillis();
        hblv.checkHost("202.24.34.55", 200);
        long time200threads = System.currentTimeMillis() - ini200threads;
        Assert.assertFalse("Tiempo de 200 Thread debe ser menor a 80 threads", time80threads<time200threads);
    }

    @Test
    public void shouldCheckMalicious(){
        HostBlackListsValidator hblv=new HostBlackListsValidator();
        List<Integer> integers = hblv.checkHost("202.24.34.55", 200);
        Assert.assertEquals(integers.size(),5);
    }
    @Test
    public void shouldNotCheckMalicious(){
        HostBlackListsValidator hblv=new HostBlackListsValidator();
        List<Integer> integers = hblv.checkHost("209.24.34.55", 200);
        Assert.assertEquals(integers.size(),0);
    }
}
