package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.ImageValidator;
import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.MovieService;

@Controller
public class MovieController {
	@Autowired 
	private MovieRepository movieRepository;
	
	@Autowired
	private ArtistRepository artistRepository;

	@Autowired 
	private ArtistService artistService;

	@Autowired 
	private MovieValidator movieValidator;
	
	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private ImageValidator imageValidator;

	@Autowired
	private MovieService movieService;

	@GetMapping(value = "/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "admin/formNewMovie";
	}


	

	@GetMapping(value = "/admin/formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieService.findMovieById(id));
		return "admin/formUpdateMovie.html";
	}

	@GetMapping(value = "/admin/indexMovie")
	public String indexMovie() {
		return "admin/indexMovie.html";
	}
	
	@GetMapping(value = "/admin/manageMovies")
	public String manageMovies(Model model) {
		model.addAttribute("movies", this.movieRepository.findAll());
		return "admin/manageMovies.html";
	}
	
	@GetMapping(value = "/admin/setDirectorToMovie/{directorId}/{movieId}")
	public String setDirectorToMovie(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieService.setDirectorToMovie(directorId, movieId);
		model.addAttribute("movie", movie);
		return "admin/formUpdateMovie.html";
	}
	
	
	@GetMapping(value = "/admin/addDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artists", this.artistService.findAllArtist());
		model.addAttribute("movie", this.movieService.findMovieById(id));
		return "admin/directorsToAdd.html";
	}

	@PostMapping("/admin/movie")
	public String newMovie(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, Model model,
	 @RequestParam("file") MultipartFile image) throws IOException {
		
		this.imageValidator.validate(image, bindingResult);
		this.movieValidator.validate(movie, bindingResult);
		if (!bindingResult.hasErrors()) {
			this.movieService.saveMovie(movie, image);
			model.addAttribute("movie", movie);
			return "movie.html";
		} else {
			return "admin/formNewMovie.html"; 
		}
	}

	@GetMapping("/movie/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieService.findMovieById(id));
		return "movie.html";
	}

	@GetMapping("/movie")
	public String getMovies(Model model) {
		model.addAttribute("movies", this.movieRepository.findAll());
		return "movies.html";
	}
	
	@GetMapping("/formSearchMovies")
	public String formSearchMovies() {
		return "formSearchMovies.html";
	}

	@PostMapping("/searchMovies")
	public String searchMovies(Model model, @RequestParam int year) {
		model.addAttribute("movies", this.movieRepository.findByYear(year));
		return "foundMovies.html";
	}
	
	@GetMapping("/admin/updateActors/{id}")
	public String updateActors(@PathVariable("id") Long id, Model model) {

		List<Artist> actorsToAdd = this.movieService.actorsToAdd(id);
		model.addAttribute("actorsToAdd", actorsToAdd);
		model.addAttribute("movie", this.movieService.findMovieById(id));

		return "admin/actorsToAdd.html";
	}



	@GetMapping("/admin/addActorToMovie/{actorId}/{movieId}")
	public String addActorToMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieService.findMovieById(movieId);
		Artist actor = this.artistService.findArtistById(actorId);
		Set<Artist> actors = movie.getActors();
		actors.add(actor);
		this.movieRepository.save(movie);
		
		List<Artist> actorsToAdd = this.movieService.actorsToAdd(movieId);
		
		model.addAttribute("movie", movie);
		model.addAttribute("actorsToAdd", actorsToAdd);

		return "admin/actorsToAdd.html";
	}
	
	@GetMapping("/admin/removeActorFromMovie/{actorId}/{movieId}")
	public String removeActorFromMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieService.findMovieById(movieId);
		Artist actor = this.artistService.findArtistById(actorId);
		Set<Artist> actors = movie.getActors();
		actors.remove(actor);
		this.movieRepository.save(movie);

		List<Artist> actorsToAdd = this.movieService.actorsToAdd(movieId);
		
		model.addAttribute("movie", movie);
		model.addAttribute("actorsToAdd", actorsToAdd);

		return "admin/actorsToAdd.html";
	}

	@PostMapping(value = "/admin/addImage")
	public String addImage(@RequestParam("file") MultipartFile image, @RequestParam("movie") Long movieId, Model model)
			throws IOException {
		Movie movie = this.movieService.findMovieById(movieId);
		this.movieService.addImage(movie, image);
		model.addAttribute("movie", movie);
		return "admin/formUpdateMovie.html";
	}

	
	@GetMapping(value = "/admin/removeImage/{movieId}/{imageId}")
	public String removeImage(@PathVariable("movieId") Long movieId, @PathVariable("imageId") Long imageId,
			Model model) {
		this.movieService.removeImage(movieId, imageId);
		model.addAttribute("movie", this.movieService.findMovieById(movieId));
		return "admin/formUpdateMovie.html";
	}

	
}
