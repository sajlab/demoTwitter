package com.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import twitter4j.TwitterException;

/**
 * The Class TwitterService.
 */
@Service
public class TwitterService {

	@Autowired
	private TwitterStreamService streamService;

	@Autowired
	private TwitterRepository repo;

	/**
	 * Find all.
	 *
	 * @return the list
	 */
	public List<TweetEntity> findAll() {
		streamService.run();
		return repo.findAll();
	}

	/**
	 * Find all by user and validate.
	 *
	 * @param user the user
	 * @return the list
	 */
	public List<TweetEntity> findAllByUserAndValidate(String user) {
		streamService.run();
		return repo.findAllByUserAndValidate(user, Boolean.TRUE);
	}

	/**
	 * Validate.
	 *
	 * @param idTweet the id tweet
	 * @return true, if successful
	 */
	public boolean validate(String idTweet) {
		boolean result = false;
		streamService.run();
		Optional<TweetEntity> optionalTweet = repo.findById(Long.valueOf(idTweet));
		if (optionalTweet.isPresent()) {
			optionalTweet.get().setValidate(Boolean.TRUE);
			repo.save(optionalTweet.get());
			result = true;
		}
		return result;
	}

	/**
	 * Top trending. Default value 10
	 *
	 * @param top the top
	 * @return the list
	 * @throws TwitterException the twitter exception
	 */
	public List<String> topTrending(String top) throws TwitterException {
		int topHashtags = 10;

		try {
			topHashtags = Integer.valueOf(top);
		} catch (Exception e) {
			;
		}

		return streamService.topTrending(topHashtags);
	}
}
