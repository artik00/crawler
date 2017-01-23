package com.bizzabo.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

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
			counter = CounterOfCrawledSites.getCounter(totalToScan);
			try {
				sharedQueue.put(url);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=0;i< workers;i++){
				executor.execute(new SingleSiteCrawler(i,sharedQueue));
			}
	        executor.shutdown();
	        while (!executor.isTerminated()) {
	        }
	        System.out.println("Finished all threads");
			

		}
		/**
		 * Start your code here
		 */
		System.out.println("Implement me");
	}

}
