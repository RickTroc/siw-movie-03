package it.uniroma3.siw.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.service.UserService;

@Controller
public class UserController {
    @Autowired UserService userService;
    @Autowired MovieRepository movieRepository;

    @PostMapping("/movie")
    public String newMovie(@Valid @ModelAttribute("movie") Movie movie, 
                            BindingResult bindingResult, Model model){
        if(!bindingResult.hasErrors()) {
            this.movieRepository.save(movie);
            model.addAttribute("movie", movie);
            return "movie.html";
        } else{
            return "formNewMovie.html";
        }
                        
    }
    
}
