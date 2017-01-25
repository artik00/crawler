package com.bizzabo.task;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SingleSiteCrawler implements Runnable{

	private int threadNumber;
	private BlockingQueue<String> urlQueueToVisit;
	volatile boolean finishedScanning = false;
	
	@Override
	public void run() {
		try {
			// we want to continue crawling as long as the list is not empty
			// or we didn't visit all the sites 
			while(!urlQueueToVisit.isEmpty() || CounterOfCrawledSites.shouldContinue()){ 
				scanTheURL();
			}
		} catch (InterruptedException e) {
			 Thread.currentThread().interrupt();
		}
	}
	/**  Contructor , will construct a single site crawler	
	 * 
	 * @param number - just a number of the thread , for printing
	 * @param sitesToCrawl - a list of sites to work on
	 */
	public SingleSiteCrawler(int number,BlockingQueue<String> sitesToCrawl){
		this.threadNumber = number;
		this.urlQueueToVisit = sitesToCrawl;
	}
	/** Scans the urls if there are in the queue	
	 * 
	 * @throws InterruptedException
	 */
	private void scanTheURL() throws InterruptedException{
		//this will mark the thread as idle
		CounterOfCrawledSites.incrementNumberOfIdleThreads();
		//try to get a url from the list
		String urlToVisit = urlQueueToVisit.poll(10, TimeUnit.SECONDS);
		if(urlToVisit!=null){
			CounterOfCrawledSites.decrementNumberOfIdleThreads();
			Site siteToCrawl = new Site(urlToVisit);
			// if we could not process the site we don't want to mark it as visited
			if(siteToCrawl.getTitle() != null){
				if(!CounterOfCrawledSites.getCounter().addVisitedSite(urlToVisit)){
					return;
				}
			}
			addNewURLtoQueue(siteToCrawl);
			if(siteToCrawl.getTitle() != null){
				System.out.println("Thread number : " + threadNumber + " Title : " + siteToCrawl.getTitle());
			}
		}else{// we didn't get a site from the queue
			//
			CounterOfCrawledSites.decrementNumberOfIdleThreads();
		}
	}
	/** Iterates on the documents and finds all the urls inside the html file	
	 *  Will add some urls to the list of future sites to crawl
	 * @param site
	 */
	private void addNewURLtoQueue(Site site){
		List<String> urls = site.findAllUrlsInsidePage(site.getDoc());
		for(String tempUrl : urls){
			synchronized (this) {
				if(!CounterOfCrawledSites.isAlreadyVisited(tempUrl) && !urlQueueToVisit.contains(tempUrl) && urlQueueToVisit.size() < CounterOfCrawledSites.getMaxNumberOfSitesToCrawl()){
					urlQueueToVisit.add(tempUrl);
				}
			}

		}
	}
	 
}
