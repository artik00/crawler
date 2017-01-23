package com.bizzabo.task;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterOfCrawledSites {
	private final int maxNumberOfSitesToCrawl;
	public AtomicInteger counter = new AtomicInteger(0);
	ConcurrentSkipListSet<String> visitedUrls = new ConcurrentSkipListSet<>();
	
	/** Will return false once the max number of sites were visited	
	 * 
	 * 
	 * @param url
	 * @return
	 */
	public boolean addVisitedSite(String url){
		synchronized (this) {
			visitedUrls.add(url);
			if(maxNumberOfSitesToCrawl == counter.getAndIncrement()){
				return false;
			}
			else{
				return true;
			}
		}
		
	}
	private static CounterOfCrawledSites instance = null;
	
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
}
