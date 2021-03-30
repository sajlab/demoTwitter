package com.demo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import twitter4j.Status;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStreamFactory;

/**
 * The Class TwitterStreamService.
 */
@Service
@Slf4j
public class TwitterStreamService {

	@Getter
	private boolean running;

	@Autowired
	private TwitterRepository repo;
	
	private List<String> languages = Arrays.asList(new String[]{"es", "fr", "it"});

	
	/**
	 * Run Twitter Stream service after startup.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void runTwitterStreamService() {
		if (this != null) {
			this.run();
		}
	}

	/**
	 * Run.
	 */
	
	public void run() {
		if (running) {
			return;
		}
		
		new TwitterStreamFactory()
				.getInstance()
				.onStatus(this::saveEntity)
				.sample();
		
		running = Boolean.TRUE;
	}
	

	/**
	 * Save tweet.
	 */
	
	private void saveEntity(Status status) {
		if (status == null) return;
		
		try {
			boolean language = languages.contains(status.getLang());
			if (language && status.getUser().getFollowersCount() >= 1500) {
//				log.debug(TweetEntity.valueOf(status).toString());
				repo.save(TweetEntity.valueOf(status));
			}
		} catch (Exception e) {
			log.debug(e.toString());
		}
	}
	
	/**
	 * Top trending.
	 *
	 * @param topHashtags the topHashtags
	 * @return the list
	 * @throws TwitterException the twitter exception
	 */
	
	public List<String> topTrending(int topHashtags) throws TwitterException{
		TwitterFactory tf = new TwitterFactory();
		Twitter twitter = tf.getInstance();
		Trends trends = twitter.getPlaceTrends(1);

		return Arrays.stream(trends.getTrends())
				.map(trend -> trend.getName())
				.limit(topHashtags)
				.collect(Collectors.toList());
	}

	
	
	
}
