package com.bizzabo.task;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CounterOfCrawledSites{
	private static final ReadWriteLock lock =  new ReentrantReadWriteLock(); 
	private static int maxNumberOfSitesToCrawl = 0;
	private static int maxNumberOfThreads = 0;

	volatile public static int numberOfIdleThreads = 0;

	public AtomicInteger counter = new AtomicInteger(0);
	private static boolean shouldContinue = true;
	private static ConcurrentSkipListSet<String> visitedUrls = new ConcurrentSkipListSet<>();
	
	private static CounterOfCrawledSites instance = null;
	
	/** Will add a new url to a visited sites array	
	 *  
	 * 
	 * @param url - to add to the list
	 * @return will return false if we visited enough sites already
	 */
	public boolean addVisitedSite(String url){
		synchronized (this) {
			visitedUrls.add(url);
			if(maxNumberOfSitesToCrawl <= counter.getAndIncrement()){
				stop();
				return false;
			}
			else{
				return true;
			}
		}
		
	}
	public static int getMaxNumberOfSitesToCrawl() {
		return maxNumberOfSitesToCrawl;
	}
	/** Singleton of the counter which monitors the number of running threads
	 * and counts the number of urls already visited.	
	 * 
	 * @param maxNumber - Of urls to visit
	 * @param maxnumberOfThreads - number of threads , 
	 * we need this in order to know if all the threads are in idle mode
	 * @return instance of the counter
	 */
	public static CounterOfCrawledSites getCounter(int maxNumber,int maxnumberOfThreads){
		if(instance==null){
			synchronized (CounterOfCrawledSites.class) {
				if(instance==null){
					instance = new CounterOfCrawledSites(maxNumber,maxnumberOfThreads);
				}
			}
		}
		return instance;
	}
	
	public static CounterOfCrawledSites getCounter(){
		return instance;
	}
	
	private CounterOfCrawledSites(int maxNumber,int maxNumberOfThreads){
		maxNumberOfSitesToCrawl = maxNumber;
		CounterOfCrawledSites.maxNumberOfThreads = maxNumberOfThreads;
	}
	/** This functions checks whether the crawling should go on	
	 * 
	 * @return
	 */
	public static boolean shouldContinue(){
		lock.readLock().lock();
		boolean answer = shouldContinue;
		lock.readLock().unlock();
		if(numberOfIdleThreads==maxNumberOfThreads){
			answer = false;
		}
		return answer;
	}
	/** This function changes  the shouldContinue to false	
	 *  meaning we stop the execution of all 
	 */
	public static void stop(){
		lock.writeLock().lock();
		shouldContinue = false;
		lock.writeLock().unlock();
	}
	/** This functions checks if we already have visited a given site	
	 * 
	 * @param url - to check in list
	 * @return - true if was visited , false otherwise
	 */
	public static boolean isAlreadyVisited(String url){
		return visitedUrls.contains(url);
	}

	
	public static int getNumberOfIdleThreads() {
		return numberOfIdleThreads;
	}
	
	public static void decrementNumberOfIdleThreads(){
		numberOfIdleThreads--;
	}
	
	public static void incrementNumberOfIdleThreads(){
		numberOfIdleThreads++;
	}
}
