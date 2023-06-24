package it.uniroma3.siw.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ReviewRepository;

@Service
public class ReviewService {
   @Autowired
	private ReviewRepository reviewRepository;
	
	@Transactional
	public Iterable<Review> findfindMovieReviewsWithoutUser(Long movieId, Long userId){
		return this.reviewRepository.findMovieReviewsWithoutUser(movieId, userId);
	}
 
	public Review findReview(Long id){
		return this.reviewRepository.findById(id).get();
	}

	public boolean checkUserReview(User user, Long reviewId){
		Review review  =this.findReview(reviewId);
		if(user.equals(review.getUser()))
			return true;
		return false;
	}

	public void removeReview(Long reviewId){
		Review review = this.findReview(reviewId);
		Movie movie = review.getMovie();
		User user = review.getUser();
		movie.getReviews().remove(review);
		user.getReviews().remove(movie);
		this.reviewRepository.deleteById(reviewId);
	}

	public void save(Review review){
		this.reviewRepository.save(review);
	}

}
