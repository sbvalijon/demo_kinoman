package uz.oltinolma.consumer.mvc.movie.service;

import uz.oltinolma.consumer.mvc.movie.Movie;

import java.util.List;
import java.util.UUID;

public interface MovieService {

    String movieInfoById(UUID id);

    void insert(String json);

    List<Movie> list();
}