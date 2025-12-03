package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MockMovieService mockMovieService;
    private ReviewService mockReviewService;

    // Custom mock service for testing
    private static class MockMovieService extends MovieService {
        private final List<Movie> testMovies;

        public MockMovieService() {
            this.testMovies = Arrays.asList(
                new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                new Movie(3L, "Comedy Movie", "Comedy Director", 2021, "Comedy", "Comedy description", 95, 3.5)
            );
        }

        @Override
        public List<Movie> getAllMovies() {
            return testMovies;
        }
        
        @Override
        public Optional<Movie> getMovieById(Long id) {
            return testMovies.stream().filter(movie -> movie.getId() == id).findFirst();
        }

        @Override
        public List<Movie> searchMovies(String name, Long id, String genre) {
            return testMovies.stream()
                    .filter(movie -> name == null || name.trim().isEmpty() || 
                            movie.getMovieName().toLowerCase().contains(name.toLowerCase()))
                    .filter(movie -> id == null || movie.getId() == id)
                    .filter(movie -> genre == null || genre.trim().isEmpty() || 
                            movie.getGenre().toLowerCase().contains(genre.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MockMovieService();
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Should return movies view for getMovies")
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(3, movies.size());
    }

    @Test
    @DisplayName("Should return movie details view for valid movie ID")
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
        
        Movie movie = (Movie) model.getAttribute("movie");
        assertNotNull(movie);
        assertEquals("Test Movie", movie.getMovieName());
    }

    @Test
    @DisplayName("Should return error view for invalid movie ID")
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
        
        String title = (String) model.getAttribute("title");
        String message = (String) model.getAttribute("message");
        assertEquals("Movie Not Found", title);
        assertTrue(message.contains("999"));
    }

    @Test
    @DisplayName("Should return all movies for search API with no criteria")
    public void testSearchMoviesApiNoCriteria() {
        ResponseEntity<List<Movie>> response = moviesController.searchMovies(null, null, null);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
    }

    @Test
    @DisplayName("Should search movies by name via API")
    public void testSearchMoviesApiByName() {
        ResponseEntity<List<Movie>> response = moviesController.searchMovies("Test", null, null);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Movie", response.getBody().get(0).getMovieName());
    }

    @Test
    @DisplayName("Should search movies by ID via API")
    public void testSearchMoviesApiById() {
        ResponseEntity<List<Movie>> response = moviesController.searchMovies(null, 2L, null);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Action Movie", response.getBody().get(0).getMovieName());
    }

    @Test
    @DisplayName("Should search movies by genre via API")
    public void testSearchMoviesApiByGenre() {
        ResponseEntity<List<Movie>> response = moviesController.searchMovies(null, null, "Action");
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Action Movie", response.getBody().get(0).getMovieName());
    }

    @Test
    @DisplayName("Should return empty list for no matches via API")
    public void testSearchMoviesApiNoMatches() {
        ResponseEntity<List<Movie>> response = moviesController.searchMovies("NonExistent", null, null);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }

    @Test
    @DisplayName("Should return search page with all movies when no search criteria")
    public void testSearchMoviesPageNoCriteria() {
        String result = moviesController.searchMoviesPage(null, null, null, model);
        
        assertEquals("movies-search", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(3, movies.size());
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("treasure chest"));
        
        Boolean hasSearched = (Boolean) model.getAttribute("hasSearched");
        assertFalse(hasSearched);
    }

    @Test
    @DisplayName("Should return search page with filtered results when search criteria provided")
    public void testSearchMoviesPageWithCriteria() {
        String result = moviesController.searchMoviesPage("Test", null, null, model);
        
        assertEquals("movies-search", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("Found 1 movie"));
        
        Boolean hasSearched = (Boolean) model.getAttribute("hasSearched");
        assertTrue(hasSearched);
        
        String searchName = (String) model.getAttribute("searchName");
        assertEquals("Test", searchName);
    }

    @Test
    @DisplayName("Should return search page with no results message when no matches found")
    public void testSearchMoviesPageNoMatches() {
        String result = moviesController.searchMoviesPage("NonExistent", null, null, model);
        
        assertEquals("movies-search", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(0, movies.size());
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("No treasure found"));
        
        Boolean hasSearched = (Boolean) model.getAttribute("hasSearched");
        assertTrue(hasSearched);
    }

    @Test
    @DisplayName("Should handle empty string search criteria")
    public void testSearchMoviesPageEmptyStrings() {
        String result = moviesController.searchMoviesPage("", null, "", model);
        
        assertEquals("movies-search", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(3, movies.size()); // Should return all movies
        
        Boolean hasSearched = (Boolean) model.getAttribute("hasSearched");
        assertFalse(hasSearched); // Empty strings should be treated as no search
    }

    @Test
    @DisplayName("Should handle whitespace-only search criteria")
    public void testSearchMoviesPageWhitespaceOnly() {
        String result = moviesController.searchMoviesPage("   ", null, "   ", model);
        
        assertEquals("movies-search", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(3, movies.size()); // Should return all movies
        
        Boolean hasSearched = (Boolean) model.getAttribute("hasSearched");
        assertFalse(hasSearched); // Whitespace-only should be treated as no search
    }

    @Test
    @DisplayName("Should integrate with movie service correctly")
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        Optional<Movie> movie = mockMovieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals("Test Movie", movie.get().getMovieName());
        
        List<Movie> searchResults = mockMovieService.searchMovies("Action", null, null);
        assertEquals(1, searchResults.size());
        assertEquals("Action Movie", searchResults.get(0).getMovieName());
    }
}
