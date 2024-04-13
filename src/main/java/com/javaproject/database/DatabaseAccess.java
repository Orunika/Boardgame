package com.javaproject.database;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import com.javaproject.beans.BoardGame;
import com.javaproject.beans.Review;

@Repository
public class DatabaseAccess {

    private static final Logger logger = Logger.getLogger(DatabaseAccess.class.getName());

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    public List<String> getAuthorities() {
        logger.log(Level.INFO, "Fetching authorities from the database");
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "SELECT DISTINCT authority FROM authorities";
        List<String> authorities = jdbc.queryForList(query, namedParameters, String.class);
        return authorities;
    }

    public List<BoardGame> getBoardGames() {
        logger.log(Level.INFO, "Fetching all board games from the database");
        String query = "SELECT * FROM boardgames";
        BeanPropertyRowMapper<BoardGame> boardgameMapper = new BeanPropertyRowMapper<>(BoardGame.class);
        List<BoardGame> boardgames = jdbc.query(query, boardgameMapper);
        return boardgames;
    }

    public BoardGame getBoardGame(Long id) {
        logger.log(Level.INFO, "Fetching board game with id: " + id + " from the database");
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "SELECT * FROM boardgames WHERE id = :id";
        namedParameters.addValue("id", id);
        BeanPropertyRowMapper<BoardGame> boardgameMapper = new BeanPropertyRowMapper<>(BoardGame.class);
        List<BoardGame> boardgames = jdbc.query(query, namedParameters, boardgameMapper);
        return boardgames.isEmpty() ? null : boardgames.get(0);
    }

    public List<Review> getReviews(Long id) {
        logger.log(Level.INFO, "Fetching reviews for board game with id: " + id + " from the database");
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "SELECT * FROM reviews WHERE gameId = :id";
        namedParameters.addValue("id", id);
        BeanPropertyRowMapper<Review> reviewMapper = new BeanPropertyRowMapper<>(Review.class);
        List<Review> reviews = jdbc.query(query, namedParameters, reviewMapper);
        return reviews.isEmpty() ? null : reviews;
    }

    public Long addBoardGame(BoardGame boardgame) {
        logger.log(Level.INFO, "Adding new board game to the database: " + boardgame.getName());
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "INSERT INTO boardgames (name, level, minPlayers, maxPlayers, gameType) VALUES (:name, :level, :minPlayers, :maxPlayers, :gameType)";
        namedParameters.addValue("name", boardgame.getName())
                .addValue("level", boardgame.getLevel())
                .addValue("minPlayers", boardgame.getMinPlayers())
                .addValue("maxPlayers", boardgame.getMaxPlayers())
                .addValue("gameType", boardgame.getGameType());
        KeyHolder generatedKey = new GeneratedKeyHolder();
        int returnValue = jdbc.update(query, namedParameters, generatedKey);
        Long boardGameId = (Long) generatedKey.getKey();
        return (returnValue > 0) ? boardGameId : 0;
    }

    public int addReview(Review review) {
        logger.log(Level.INFO, "Adding new review for board game with id: " + review.getGameId());
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "INSERT INTO reviews (gameId, text) VALUES (:gameId, :text)";
        namedParameters.addValue("gameId", review.getGameId())
                .addValue("text", review.getText());
        return jdbc.update(query, namedParameters);
    }

    public int deleteReview(Long id) {
        logger.log(Level.INFO, "Deleting review with id: " + id);
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "DELETE FROM reviews WHERE id = :id";
        namedParameters.addValue("id", id);
        return jdbc.update(query, namedParameters);
    }

    public Review getReview(Long id) {
        logger.log(Level.INFO, "Fetching review with id: " + id + " from the database");
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "SELECT * FROM reviews WHERE id = :id";
        namedParameters.addValue("id", id);
        BeanPropertyRowMapper<Review> reviewMapper = new BeanPropertyRowMapper<>(Review.class);
        List<Review> reviews = jdbc.query(query, namedParameters, reviewMapper);
        return reviews.isEmpty() ? null : reviews.get(0);
    }

    public int editReview(Review review) {
        logger.log(Level.INFO, "Editing review with id: " + review.getId());
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "UPDATE reviews SET text = :text WHERE id = :id";
        namedParameters.addValue("text", review.getText())
                .addValue("id", review.getId());
        return jdbc.update(query, namedParameters);
    }
}
