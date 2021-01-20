package edu.eci.arsw.blacklistvalidator.Threads;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Black list thread.
 */
public class BlackListThread extends Thread {
    private static final int BLACK_LIST_ALARM_COUNT = 5;
    private AtomicInteger ocurrencesCount,checkListsCount;
    private int initialServ,finalServ;
    private String ipAddress;
    private List<Integer> blackListOcurrences;
    private HostBlacklistsDataSourceFacade facade;


    /**
     * Instantiates a new Black list thread.
     *
     * @param ipAddress           the ip address
     * @param facade              the facade
     * @param blackListOcurrences the black list ocurrences
     * @param checkListsCount     the check lists count
     * @param ocurrencesCount     the ocurrences count
     * @param initalServ          the inital serv
     * @param finalServ           the final serv
     */
    public BlackListThread(String ipAddress, HostBlacklistsDataSourceFacade facade,List<Integer> blackListOcurrences,AtomicInteger checkListsCount,AtomicInteger ocurrencesCount, int initalServ, int finalServ){
        this.ipAddress = ipAddress;
        this.facade = facade;
        this.initialServ = initalServ;
        this.finalServ = finalServ;
        this.blackListOcurrences = blackListOcurrences;
        this.ocurrencesCount = ocurrencesCount;
        this.checkListsCount = checkListsCount;
    }

    /**
     * Verify if a ipAddress is malicious in a Server's range
     */
    public void run(){

        for (int i=initialServ;i<finalServ && ocurrencesCount.get()<BLACK_LIST_ALARM_COUNT;i++){

            checkListsCount.getAndIncrement();
            if (facade.isInBlackListServer(i, ipAddress)){
                blackListOcurrences.add(i);
                ocurrencesCount.getAndIncrement();
            }
        }


    }

    /**
     * Gets ocurrences count.
     *
     * @return the ocurrences count
     */
    public AtomicInteger getOcurrencesCount() {
        return ocurrencesCount;
    }

    /**
     * Sets ocurrences count.
     *
     * @param ocurrencesCount the ocurrences count
     */
    public void setOcurrencesCount(AtomicInteger ocurrencesCount) {
        this.ocurrencesCount = ocurrencesCount;
    }

    /**
     * Gets initial serv.
     *
     * @return the initial serv
     */
    public int getInitialServ() {
        return initialServ;
    }

    /**
     * Sets initial serv.
     *
     * @param initialServ the initial serv
     */
    public void setInitialServ(int initialServ) {
        this.initialServ = initialServ;
    }

    /**
     * Gets final serv.
     *
     * @return the final serv
     */
    public int getFinalServ() {
        return finalServ;
    }

    /**
     * Sets final serv.
     *
     * @param finalServ the final serv
     */
    public void setFinalServ(int finalServ) {
        this.finalServ = finalServ;
    }

    /**
     * Gets ip address.
     *
     * @return the ip address
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets ip address.
     *
     * @param ipAddress the ip address
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Gets black list ocurrences.
     *
     * @return the black list ocurrences
     */
    public List<Integer> getBlackListOcurrences() {
        return blackListOcurrences;
    }

    /**
     * Sets black list ocurrences.
     *
     * @param blackListOcurrences the black list ocurrences
     */
    public void setBlackListOcurrences(List<Integer> blackListOcurrences) {
        this.blackListOcurrences = blackListOcurrences;
    }

    /**
     * Gets check lists count.
     *
     * @return the check lists count
     */
    public AtomicInteger getCheckListsCount() {
        return checkListsCount;
    }

    /**
     * Sets check lists count.
     *
     * @param checkListsCount the check lists count
     */
    public void setCheckListsCount(AtomicInteger checkListsCount) {
        this.checkListsCount = checkListsCount;
    }

    /**
     * Gets facade.
     *
     * @return the facade
     */
    public HostBlacklistsDataSourceFacade getFacade() {
        return facade;
    }

    /**
     * Sets facade.
     *
     * @param facade the facade
     */
    public void setFacade(HostBlacklistsDataSourceFacade facade) {
        this.facade = facade;
    }
}
