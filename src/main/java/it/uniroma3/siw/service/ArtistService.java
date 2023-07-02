package it.uniroma3.siw.service;

import java.io.IOException;
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
	
}
