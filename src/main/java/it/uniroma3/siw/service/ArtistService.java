package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;

@Service
public class ArtistService {
    @Autowired
    private ArtistRepository artistRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired 
	private MovieService movieService;


    @Transactional
    public Iterable<Artist> findAllArtist(){
        return this.artistRepository.findAll();
    }

	@Transactional
    public Artist findArtistById(Long id){
        return this.artistRepository.findById(id).get();
    }

    
	public boolean existsByNameAndSurname(String name, String surname) {
		return this.artistRepository.existsByNameAndSurname(name, surname);
	}
	
	@Transactional
	public void saveArtist(@Valid Artist artist, MultipartFile image) throws IOException {
		Image artistImage = new Image(image.getBytes());
		this.imageRepository.save(artistImage);
		artist.setArtistPicture(artistImage);
		this.artistRepository.save(artist);
	}
	
	@Transactional
	public void deleteArtist(Long artistId) {
		Artist artist = this.findArtistById(artistId);
		Set<Movie> movies = artist.getStarredMovies();
		for(Movie movie : movies) {
			movie.getActors().remove(artist);
		}
		List<Movie> directed = artist.getDirectorOf();
		for(Movie movie : directed){
			movie.setDirector(null);
		}

		this.artistRepository.delete(artist);
	}
	
	@Transactional
	public Artist updateArtist(Artist oldArtist, Artist newArtist) {
		oldArtist.setName(newArtist.getName());
		oldArtist.setSurname(newArtist.getSurname());
		oldArtist.setDateOfBirth(newArtist.getDateOfBirth());
		this.artistRepository.save(oldArtist);
		return oldArtist;
	}
	

    @Transactional
    public List<Movie> directedMovieToAdd(Long id) {
        List<Movie> moviesToAdd = new ArrayList<>();

		for (Movie m : this.movieService.findMovieNotDirected(id)) {
			moviesToAdd.add(m);
		}
		return moviesToAdd;
    }
  
	@Transactional
    public List<Movie> starredMovieToAdd(Long id) {
        List<Movie> moviesToAdd = new ArrayList<>();

		for (Movie m : this.movieService.findMovieNotStarred(id)) {
			moviesToAdd.add(m);
		}
		return moviesToAdd;
    }
	@Transactional
    public Artist addDirectedMovieToArtist(Long movieId, Long artistId) {
        Artist artist = this.findArtistById(artistId);
        Movie movie = this.movieService.findMovieById(movieId);
        artist.getDirectedMovies().add(movie);
        movie.setDirector(artist);
        this.movieRepository.save(movie);
        return artist;
    }
    @Transactional
    public Artist removeDirectedMovieFromArtist(Long artistId, Long movieId) {
        Movie movie = this.movieService.findMovieById(movieId);
        Artist artist = this.findArtistById(artistId);
        artist.getDirectedMovies().remove(movie);
        movie.setDirector(null);
        this.movieRepository.save(movie);
        return artist;
    }
	@Transactional
    public Artist addStarredMovieToArtist(Long movieId, Long artistId) {
        Artist artist = this.findArtistById(artistId);
        Movie movie = this.movieService.findMovieById(movieId);
        artist.getStarredMovies().add(movie);
        movie.getActors().add(artist);
        this.artistRepository.save(artist);
        return artist;
    }
   
    @Transactional
    public Artist removeStarredMovieFromArtist(Long artistId, Long movieId) {
        Movie movie = this.movieService.findMovieById(movieId);
        Artist artist = this.findArtistById(artistId);
        artist.getStarredMovies().remove(movie);
        movie.getActors().remove(artist);
        this.artistRepository.save(artist);
        return artist;
    }

    @Transactional
    public Iterable<Movie> findDirectedMovies(Long artistId){
		return this.movieRepository.findDirectedMovies(artistId);
	}

    @Transactional
    public List<Movie> getDirectedMovies(Long artistId) {
        List<Movie> out = new LinkedList<>();

        for (Movie m : this.findDirectedMovies(artistId)) {
            out.add(m);
        }
        return out;
    }



}
