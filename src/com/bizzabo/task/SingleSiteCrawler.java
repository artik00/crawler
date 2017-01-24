package com.bizzabo.task;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SingleSiteCrawler implements Runnable{

	private int threadNumber;
	private BlockingQueue<String> urlQueueToVisit;
	
	
	@Override
	public void run() {
		try {
			while(!urlQueueToVisit.isEmpty() || CounterOfCrawledSites.shouldContinue()){ 
				logic();
			}
		} catch (InterruptedException e) {
			 Thread.currentThread().interrupt();
		}
	}

	public SingleSiteCrawler(int number,BlockingQueue<String> sitesToCrawl){
		this.threadNumber = number;
		this.urlQueueToVisit = sitesToCrawl;
	}
	
	private void logic() throws InterruptedException{
		String urlToVisit = urlQueueToVisit.poll(10, TimeUnit.SECONDS);
		if(urlToVisit!=null){ // we didn't get a site from the queue
			Site siteToCrawl = new Site(urlToVisit);
			if(!CounterOfCrawledSites.getCounter().addVisitedSite(urlToVisit)){
				return;
			}
			addNewURLtoQueue(siteToCrawl);
			if(siteToCrawl.getTitle() != null){
				System.out.println("Thread number : " + threadNumber + " Title : " + siteToCrawl.getTitle());
			}
		}
	}
	
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
