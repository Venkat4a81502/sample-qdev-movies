package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Should return all movies when no search criteria provided")
    public void testSearchMoviesWithNoCriteria() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        
        assertNotNull(results);
        assertEquals(12, results.size()); // Based on the movies.json file
    }

    @Test
    @DisplayName("Should return all movies when empty search criteria provided")
    public void testSearchMoviesWithEmptyCriteria() {
        List<Movie> results = movieService.searchMovies("", null, "");
        
        assertNotNull(results);
        assertEquals(12, results.size());
    }

    @Test
    @DisplayName("Should search movies by name (case insensitive)")
    public void testSearchMoviesByName() {
        // Test exact match
        List<Movie> results = movieService.searchMovies("The Prison Escape", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        // Test partial match
        results = movieService.searchMovies("prison", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        // Test case insensitive
        results = movieService.searchMovies("PRISON", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        // Test multiple matches
        results = movieService.searchMovies("The", null, null);
        assertTrue(results.size() > 1);
        assertTrue(results.stream().allMatch(movie -> 
            movie.getMovieName().toLowerCase().contains("the")));
    }

    @Test
    @DisplayName("Should search movies by ID")
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should search movies by genre (case insensitive)")
    public void testSearchMoviesByGenre() {
        // Test exact match
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("drama")));

        // Test partial match
        results = movieService.searchMovies(null, null, "Action");
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("action")));

        // Test case insensitive
        results = movieService.searchMovies(null, null, "DRAMA");
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    @DisplayName("Should search movies by multiple criteria")
    public void testSearchMoviesByMultipleCriteria() {
        // Search by name and genre
        List<Movie> results = movieService.searchMovies("The", null, "Drama");
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(movie -> 
            movie.getMovieName().toLowerCase().contains("the") &&
            movie.getGenre().toLowerCase().contains("drama")));

        // Search by ID and name (should match only if both criteria match)
        results = movieService.searchMovies("The Prison Escape", 1L, null);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        // Search with conflicting criteria (should return empty)
        results = movieService.searchMovies("The Prison Escape", 2L, null);
        assertEquals(0, results.size());
    }

    @Test
    @DisplayName("Should return empty list when no movies match search criteria")
    public void testSearchMoviesNoMatches() {
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        assertNotNull(results);
        assertEquals(0, results.size());

        results = movieService.searchMovies(null, 999L, null);
        assertNotNull(results);
        assertEquals(0, results.size());

        results = movieService.searchMovies(null, null, "NonExistentGenre");
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    @DisplayName("Should handle whitespace in search criteria")
    public void testSearchMoviesWithWhitespace() {
        List<Movie> results = movieService.searchMovies("  The Prison Escape  ", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        results = movieService.searchMovies(null, null, "  Drama  ");
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    @DisplayName("Should get movie by ID correctly")
    public void testGetMovieById() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals("The Prison Escape", movie.get().getMovieName());

        movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());

        movie = movieService.getMovieById(null);
        assertFalse(movie.isPresent());

        movie = movieService.getMovieById(-1L);
        assertFalse(movie.isPresent());
    }

    @Test
    @DisplayName("Should get all movies correctly")
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertEquals(12, movies.size());
        
        // Verify some known movies exist
        assertTrue(movies.stream().anyMatch(movie -> 
            movie.getMovieName().equals("The Prison Escape")));
        assertTrue(movies.stream().anyMatch(movie -> 
            movie.getMovieName().equals("The Family Boss")));
    }
}