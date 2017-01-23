package com.bizzabo.task;

import java.util.concurrent.BlockingQueue;

public class SingleSiteCrawler implements Runnable{

	private int threadNumber;
	private String title;
	private BlockingQueue<String> urlQueueToVisit;
	
	
	@Override
	public void run() {
		try {
			logic();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(title != null){
			System.out.println("Thread number : " + threadNumber + " Title : " + title);
		}
		
		
	}

	public SingleSiteCrawler(int number,BlockingQueue<String> sitesToCrawl){
		this.threadNumber = number;
		this.urlQueueToVisit = sitesToCrawl;
	}
	
	private void logic() throws InterruptedException{
		
		String urlToVisit = urlQueueToVisit.take();
		if(CounterOfCrawledSites.getCounter().addVisitedSite(urlToVisit)){
			Thread.sleep(1000);
			String newUrl = "www.blablabla"+title+".com";
			urlQueueToVisit.add(newUrl);
			title="test";
		}
	}
	 
}
