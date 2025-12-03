package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    @GetMapping("/movies/search")
    @ResponseBody
    public ResponseEntity<List<Movie>> searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("Arrr! Searching for movies with name: {}, id: {}, genre: {}", name, id, genre);
        
        try {
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            logger.info("Found {} movies matching search criteria", searchResults.size());
            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            logger.error("Error searching movies: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/movies/search-page")
    public String searchMoviesPage(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Ahoy! Displaying search page with name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> searchResults;
        String searchMessage = "";
        boolean hasSearched = false;
        
        // Check if any search parameters were provided
        if ((name != null && !name.trim().isEmpty()) || 
            id != null || 
            (genre != null && !genre.trim().isEmpty())) {
            
            hasSearched = true;
            searchResults = movieService.searchMovies(name, id, genre);
            
            if (searchResults.isEmpty()) {
                searchMessage = "Arrr! No treasure found with those search terms, matey! Try different criteria.";
            } else {
                searchMessage = "Ahoy! Found " + searchResults.size() + " movie" + 
                               (searchResults.size() == 1 ? "" : "s") + " in our treasure chest!";
            }
        } else {
            // No search performed, show all movies
            searchResults = movieService.getAllMovies();
            searchMessage = "Ahoy matey! Here be all the movies in our treasure chest. Use the search above to find specific treasures!";
        }
        
        model.addAttribute("movies", searchResults);
        model.addAttribute("searchMessage", searchMessage);
        model.addAttribute("hasSearched", hasSearched);
        model.addAttribute("searchName", name != null ? name : "");
        model.addAttribute("searchId", id != null ? id.toString() : "");
        model.addAttribute("searchGenre", genre != null ? genre : "");
        
        return "movies-search";
    }
}