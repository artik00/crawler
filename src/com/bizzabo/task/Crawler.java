package com.bizzabo.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Crawler {
	
	static private ExecutorService executor;
	static BlockingQueue<String> sharedQueue = new LinkedBlockingQueue<>();
	
	
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: Crawler baseUrl #workers #pagesToScan");
			return;
		}
		CounterOfCrawledSites counter;
		String url = args[0];
		Integer workers = Integer.parseInt(args[1]);
		Integer totalToScan = Integer.parseInt(args[2]);
		if(workers > 0){
			executor = Executors.newFixedThreadPool(workers);
			counter = CounterOfCrawledSites.getCounter(totalToScan,workers);
			try {
				sharedQueue.put(url);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			for(int i=0;i< workers;i++){
				//adding sites to the executor
				executor.execute(new SingleSiteCrawler(i,sharedQueue));
			}
			
	        try {
	        	//waiting for the counter to tell us to stop
	        	while(CounterOfCrawledSites.shouldContinue()){
	        		
	        	}
	        	// trying to terminate the threads nicely
	        	executor.awaitTermination(10, TimeUnit.SECONDS);
	        	//going really hard on them now
	        	executor.shutdownNow();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
	        System.out.println("Terminated all threads");
		}
		else{
			System.out.println("Please specify a valid number of worker threads");
		}
	}
	

}
