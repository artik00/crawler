package com.bizzabo.task;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CounterOfCrawledSites {
	private static final ReadWriteLock lock =  new ReentrantReadWriteLock(); 
	private static int maxNumberOfSitesToCrawl = 0;


	public AtomicInteger counter = new AtomicInteger(0);
	static boolean shouldContinue = true;
	private static ConcurrentSkipListSet<String> visitedUrls = new ConcurrentSkipListSet<>();
	
	private static CounterOfCrawledSites instance = null;
	
	/** Will return false once the max number of sites were visited	
	 * 
	 * 
	 * @param url
	 * @return
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
	
	public static CounterOfCrawledSites getCounter(int maxNumber){
		if(instance==null){
			synchronized (CounterOfCrawledSites.class) {
				if(instance==null){
					instance = new CounterOfCrawledSites(maxNumber);
				}
			}
		}
		return instance;
	}
	
	public static CounterOfCrawledSites getCounter(){
		return instance;
	}
	
	private CounterOfCrawledSites(int maxNumber){
		maxNumberOfSitesToCrawl = maxNumber;
	}
	
	public static boolean shouldContinue(){
		lock.readLock().lock();
		boolean answer = shouldContinue;
		lock.readLock().unlock();
		return answer;
	}
	
	public static void stop(){
		lock.writeLock().lock();
		shouldContinue = false;
		lock.writeLock().unlock();
	}
	
	public static boolean isAlreadyVisited(String url){
		return visitedUrls.contains(url);
	}
}
