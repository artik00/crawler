package com.bizzabo.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
	
	/** Finds the title of the site , by processing the document 	
	 * 	Prints Message in case of HttpStatusException
	 */
	private void processSite(){
		try {
			if(address!=null){
				doc = Jsoup.connect(address).get();
				title = findTitle(doc);
			}
		}
			
		catch (HttpStatusException ex){
				System.out.println("Ooopss... got an exception from some site");
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**	 Get the first title seen inside the html
	 * 
	 * @param doc
	 * @return
	 */
	private String findTitle(Document doc){
		if(doc!=null && doc.select("title")!=null && doc.select("title").first()!=null){
			return doc.select("title").first().text();
		}
		return null;
	}
	/** Iterate all over all the elements in the document 	
	 *  look for any possible urls in the pattern of 
	 *  "http://" or "https://"
	 * @param doc
	 * @return list of possible urls
	 */
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
