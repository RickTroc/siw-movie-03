package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ReviewService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ReviewController {
    
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CredentialsService credentialsService;


    @Transactional
    @PostMapping(value="/review")
    public String newReview(@RequestParam("movie") Long movieId, @RequestParam("score") int score, @RequestParam("title") String title, 
                            @RequestParam("comment") String comment, Model model ) {
        
        Movie movie = this.movieRepository.findById(movieId).get();
        User user = this.credentialsService.getLoggedUser();
        if(!user.getReviews().containsKey(movie)){
            Review review = new Review();
            review.setMovie(movie);
            review.setScore(score);
            review.setTitle(title);
            review.setReview(comment);
            review.setUser(user);
            this.reviewRepository.save(review);

            user.getReviews().put(movie, review);
            movie.getReviews().add(review);

            this.userRepository.save(user);
            this.movieRepository.save(movie);
        }

        model.addAttribute("user", user.getReviews().get(movie));
        model.addAttribute("movie", movie);
        model.addAttribute("movieReviews", this.reviewRepository.findMovieReviewsWithoutUser(movieId, user.getId()));

        return "movie.html";
    }
    
    @Transactional
    @GetMapping(value="deleteReview/{reviewId}")
    public String removeReview(@RequestParam("reviewId") Long reviewId, Model model) {
        User user = this.credentialsService.getLoggedUser();
        Movie movie = this.reviewService.findReview(reviewId).getMovie();
        if(this.reviewService.checkUserReview(user, reviewId)){
            this.reviewService.removeReview(reviewId);
            model.addAttribute("movie", movie);
            model.addAttribute("userReviews", movie.getReviews());
            return "movie.html";
        }
        return "failedReviewDelete.html";
    }

    @Transactional
    @GetMapping(value = "updateReview/{reviewId}")
    public String updateReview(@RequestParam("reviewId") Long reviewId, Model model){
        User user = this.credentialsService.getLoggedUser();
        if(this.reviewService.checkUserReview(user, reviewId)){
            Review review = this.reviewService.findReview(reviewId);
            model.addAttribute("movie", review.getMovie());
            model.addAttribute("userReview", review);
            return "formUpdateReview.html";
        }
        return "failedUpdateReview.html";
    }

    @Transactional
    @PostMapping(value = "updateReview")
    public String updateReview(@RequestParam("review") Long reviewId, @RequestParam("score")int score, 
                                @RequestParam("title")String title, @RequestParam("comment")String comment, Model model){

        Review review = this.reviewService.findReview(reviewId);
        review.setReview(comment);
        review.setScore(score);
        review.setTitle(title);
        this.reviewService.save(review);
        
        User user = this.credentialsService.getLoggedUser();
        Movie movie = review.getMovie();
        model.addAttribute("userReview", user.getReviews().get(movie));
        model.addAttribute("movie", movie);
        model.addAttribute("movieReviews", this.reviewRepository.findMovieReviewsWithoutUser(movie.getId(), user.getId()));
        return "movie.html";
    }
    
    @Transactional
    @GetMapping(value = "admin/deleteReview/{reviewId}")
    public String adminRemoveReview(@RequestParam("reviewId") Long reviewId, Model model){
        Movie movie = this.reviewService.findReview(reviewId).getMovie();
        this.reviewService.removeReview(reviewId);
        model.addAttribute("movie", movie);
        return "admin/formUpdateMovie.html";
    }
}
