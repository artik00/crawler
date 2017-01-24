package com.bizzabo.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Site {
	private String address;
	private String title;
	private Document doc;
	
	public Document getDoc() {
		return doc;
	}

	public String getTitle() {
		return title;
	}

	public Site(String addrss){
		this.address = addrss;
		doc = new Document(addrss);
		processSite();
	}
	
	private void processSite(){
		try {
			if(address!=null){
				doc = Jsoup.connect(address).get();
				title = findTitle(doc);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String findTitle(Document doc){
		if(doc!=null && doc.select("title")!=null){
			return doc.select("title").first().text();
		}
		return null;
	}
	
	public List<String> findAllUrlsInsidePage(Document doc){
		List<String> urls = new ArrayList<>();
		List<Element> links = doc.getElementsByTag("a");
        for (Element link : links) {
        	String possibleURL = link.attr("href");
        	if(possibleURL.matches("http://(\\w+\\.)+(\\w+)") || possibleURL.matches("https://(\\w+\\.)+(\\w+)")){
        		urls.add(possibleURL);
        	}
        }
		return urls;
	}
}
