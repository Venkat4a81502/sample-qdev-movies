# Movie Service - Spring Boot Demo Application

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with pirate-themed language and advanced search functionality.

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **Advanced Search**: Search and filter movies by name, ID, and genre with pirate-themed interface
- **REST API**: JSON endpoints for programmatic access to movie data and search functionality
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds, smooth animations, and pirate language
- **Pirate Theme**: Ahoy matey! Navigate the seven seas to find your movie treasures

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**
- **Thymeleaf** for templating
- **Bootstrap-inspired CSS** for styling

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Search**: http://localhost:8080/movies/search-page
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **Search API**: http://localhost:8080/movies/search (REST endpoint)

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller for movie endpoints
│   │       │   ├── MovieService.java         # Business logic and search functionality
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── templates/
│       │   ├── movies.html                   # Main movie listing page
│       │   ├── movies-search.html            # Search page with pirate theme
│       │   └── movie-details.html            # Movie details page
│       ├── static/css/
│       │   ├── movies.css                    # Main styling
│       │   └── search.css                    # Search form styling
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie data
│       ├── mock-reviews.json                 # Mock review data
│       └── log4j2.xml                        # Logging configuration
└── test/                                     # Comprehensive unit tests
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings and basic information.

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

### Search Movies (REST API)
```
GET /movies/search
```
Returns JSON array of movies matching the search criteria.

**Query Parameters:**
- `name` (optional): Movie name to search for (case-insensitive partial match)
- `id` (optional): Movie ID to search for (exact match)
- `genre` (optional): Movie genre to search for (case-insensitive partial match)

**Examples:**
```bash
# Search by name
curl "http://localhost:8080/movies/search?name=prison"

# Search by genre
curl "http://localhost:8080/movies/search?genre=drama"

# Search by ID
curl "http://localhost:8080/movies/search?id=1"

# Combined search
curl "http://localhost:8080/movies/search?name=the&genre=drama"

# Get all movies
curl "http://localhost:8080/movies/search"
```

**Response Format:**
```json
[
  {
    "id": 1,
    "movieName": "The Prison Escape",
    "director": "John Director",
    "year": 1994,
    "genre": "Drama",
    "description": "Two imprisoned men bond over a number of years...",
    "duration": 142,
    "imdbRating": 5.0,
    "icon": "🏛️"
  }
]
```

### Search Movies (HTML Interface)
```
GET /movies/search-page
```
Returns an HTML page with search form and results, featuring pirate-themed language.

**Query Parameters:** Same as REST API
- `name` (optional): Movie name to search for
- `id` (optional): Movie ID to search for  
- `genre` (optional): Movie genre to search for

**Example:**
```
http://localhost:8080/movies/search-page?name=action&genre=sci-fi
```

## Search Features

### Pirate-Themed Interface
- 🏴‍☠️ "Ahoy matey! Search the seven seas for movies"
- ⚓ "Set Sail & Search!" button
- 🗺️ Navigation tips for better searching
- 💰 "doubloons" rating system
- 🏴‍☠️ "No treasure found" messages for empty results

### Search Capabilities
- **Name Search**: Case-insensitive partial matching
- **ID Search**: Exact ID matching (1-12)
- **Genre Search**: Case-insensitive partial matching
- **Combined Search**: Multiple criteria with AND logic
- **Edge Case Handling**: Empty results, invalid parameters, whitespace handling

### User Experience
- **Responsive Design**: Works on all device sizes
- **Real-time Feedback**: Clear success and error messages
- **Form Persistence**: Search terms remain after submission
- **Navigation**: Easy links between search and main pages
- **Animations**: Smooth transitions and hover effects

## Error Handling

The application handles various edge cases:
- **Invalid Movie IDs**: Returns appropriate error pages
- **Empty Search Results**: Shows pirate-themed "no treasure found" message
- **Invalid Parameters**: Gracefully handles malformed requests
- **Server Errors**: Proper HTTP status codes and error responses

## Testing

Run the comprehensive test suite:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MovieServiceTest

# Run with coverage
mvn test jacoco:report
```

### Test Coverage
- **MovieService**: Search functionality, edge cases, data validation
- **MoviesController**: All endpoints, error handling, model attributes
- **Integration Tests**: End-to-end functionality testing
- **Edge Cases**: Empty results, invalid inputs, whitespace handling

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

Check that:
1. Movie IDs are between 1-12
2. Search terms are not empty or whitespace-only
3. Genre names match existing genres (Drama, Action, Comedy, etc.)

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog
- Enhance the UI/UX with more pirate themes
- Add new search features (year range, rating filters)
- Improve the responsive design
- Add more comprehensive error handling
- Extend the pirate language vocabulary

## Future Enhancements

Potential improvements:
- **Advanced Filters**: Year range, rating range, duration filters
- **Sorting Options**: Sort by rating, year, duration, alphabetical
- **Pagination**: Handle large movie catalogs
- **Favorites System**: Save favorite movies
- **User Reviews**: Allow users to add their own reviews
- **Movie Recommendations**: Suggest similar movies
- **Enhanced Pirate Theme**: More nautical terminology and animations

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.
