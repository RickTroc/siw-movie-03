package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.ImageValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;

@Service
public class MovieService {
    	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private ArtistRepository artistRepository;

	@Autowired
	private ArtistService artistService;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private ImageValidator imageValidator;


	@Transactional
	public void createNewMovie(Movie movie) {
		this.movieRepository.save(movie);
	}

	@Transactional
	public Movie findMovieById(Long id) {
		return this.movieRepository.findById(id).get();
	}

	@Transactional
	public void saveMovie(@Valid Movie movie, MultipartFile image) throws IOException {
		Image img = new Image(image.getBytes());
		this.imageRepository.save(img);

		movie.setImage(img);
		this.movieRepository.save(movie);
	}
	
	public Iterable<Movie> findAllMovies(){
		return this.movieRepository.findAll();
	}

	@Transactional
	public Movie setDirectorToMovie(Long artistId, Long movieId) {
		Artist director = this.artistService.findArtistById(artistId);
		Movie movie = this.findMovieById(movieId);
		movie.setDirector(director);
		this.movieRepository.save(movie);
		return movie;
	}
	
	public List<Artist> findActorsNotInMovie(Long movieId){
		List<Artist> actorsToAdd = new ArrayList<>();
		for (Artist a : artistRepository.findActorsNotInMovie(movieId)) {
			actorsToAdd.add(a);
		}
		return actorsToAdd;
	}

	@Transactional
    public Iterable<Movie> findMovieNotDirected(Long id) {
        return this.movieRepository.findMovieNotDirected(id);
    }

    @Transactional
    public Iterable<Movie> findMovieNotStarred(Long id) {
        return this.movieRepository.findMovieNotStarred(id);
    }

	public List<Movie> findByYear(int year) {
		return this.movieRepository.findByYear(year);
	}
	public boolean existsByTitleAndYear(String title, int year) {
		return this.movieRepository.existsByTitleAndYear(title, year);
	}

	
	@Transactional
	public List<Artist> actorsToAdd(Long movieId) {
		List<Artist> actorsToAdd = new ArrayList<>();

		for (Artist a : artistRepository.findActorsNotInMovie(movieId)) {
			actorsToAdd.add(a);
		}
		return actorsToAdd;
	}

	@Transactional
	public Movie saveActorToMovie(Long movieId, Long actorId) {
		Movie movie = this.findMovieById(movieId);
		Artist actor = this.artistService.findArtistById(actorId);
		Set<Artist> actors = movie.getActors();
		actors.add(actor);
		return this.movieRepository.save(movie);
	}
	@Transactional
	public Movie deleteActorFromMovie(Long movieId, Long actorId) {
		Movie movie = this.findMovieById(movieId);
		Artist actor = this.artistService.findArtistById(actorId);
		Set<Artist> actors = movie.getActors();
		actors.remove(actor);
		return this.movieRepository.save(movie);
	}
	
	@Transactional
	public Movie saveReviewToMovie(Long movieId, Long reviewId) {
		Review review = this.reviewService.findReview(reviewId);
		Movie movie = this.findMovieById(movieId);
		List<Review> reviews = movie.getReviews();
		reviews.add(review);
		movie.setReviews(reviews);
		review.setMovie(movie);
		return this.movieRepository.save(movie);
	}
	
	@Transactional
	public void deleteMovie(Long movieId) {
		Movie movie = this.findMovieById(movieId);
		Set<Artist> actors = movie.getActors();
		for(Artist actor : actors) {
			actor.getStarredMovies().remove(movie);
		}
		List<Review> reviews = movie.getReviews();
		for(Review review : reviews) {
			review.setMovie(null);
		}
		this.movieRepository.delete(movie);
	}
	
	@Transactional
	public Movie updateMovie(Movie oldMovie, Movie newMovie) {
		oldMovie.setTitle(newMovie.getTitle());
		oldMovie.setYear(newMovie.getYear());
		this.movieRepository.save(oldMovie);
		return oldMovie;
	}

	public void addImage(Movie movie, MultipartFile movieImg) throws IOException{
		if(this.imageValidator.isImage(movieImg) || movieImg.getSize()< imageValidator.MAX_SIZE){
			Image img = new Image(movieImg.getBytes());
			this.imageRepository.save(img);

			movie.getImages().add(img);
			this.movieRepository.save(movie);
		}
	}

	public void addPoster(Movie movie, MultipartFile movieImg) throws IOException{
		if(this.imageValidator.isImage(movieImg) || movieImg.getSize() < imageValidator.MAX_SIZE){
			Image img = new Image(movieImg.getBytes());
			this.imageRepository.save(img);
			movie.setImage(img);
			this.movieRepository.save(movie);
		}
	}

	public void removeImage(Long movieId, Long imageId){
		Image img = this.imageRepository.findById(imageId).get();
		Movie movie =  this.findMovieById(movieId);

		movie.getImages().remove(img);
		this.movieRepository.save(movie);
	}

}
