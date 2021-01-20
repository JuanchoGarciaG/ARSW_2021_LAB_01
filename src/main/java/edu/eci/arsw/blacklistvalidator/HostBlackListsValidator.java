/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.blacklistvalidator.Threads.BlackListThread;
import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;

    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int numThreads) {

        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();

        AtomicInteger checkedListsCount= new AtomicInteger(0), ocurrencesCount = new AtomicInteger(0);
        BlackListThread[] blackListThreads = new BlackListThread[numThreads];
        int totalServers = skds.getRegisteredServersCount();
        int range = totalServers/numThreads;
        int remaining = totalServers % numThreads;

        for (int i=0; i<numThreads; i++){
            int initialServer = i*range;
            int finalRange = initialServer +range;
            int lastServer = (i != numThreads-1) ? finalRange : finalRange +remaining;
            blackListThreads[i] = new BlackListThread(ipaddress,skds,blackListOcurrences,checkedListsCount,ocurrencesCount,initialServer,lastServer);
            blackListThreads[i].start();
        }
        for(BlackListThread t : blackListThreads){
            try {
                t.join();
            }catch (InterruptedException e){
                t.interrupt();
            }
        }
        if (ocurrencesCount.get()>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }

        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});

        return blackListOcurrences;
    }


    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());



}
