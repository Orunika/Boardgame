package com.javaproject.controllers;

import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.javaproject.beans.BoardGame;
import com.javaproject.beans.ErrorMessage;
import com.javaproject.database.DatabaseAccess;

@RestController
@RequestMapping("/boardgames")
public class BoardGameController {

    private static final Logger logger = Logger.getLogger(BoardGameController.class.getName());

    private DatabaseAccess da;

    public BoardGameController(DatabaseAccess da) {
        this.da = da;
    }

    @GetMapping
    public List<BoardGame> getBoardGames() {
        logger.log(Level.INFO, "Retrieving all board games");
        return da.getBoardGames();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBoardGame(@PathVariable Long id) {
        logger.log(Level.INFO, "Retrieving board game with id: " + id);
        BoardGame boardGame = da.getBoardGame(id);
        if (boardGame != null) {
            return ResponseEntity.ok(boardGame);
        } else {
            logger.log(Level.INFO, "Board game with id " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("No such record"));
        }
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> postBoardGame(@RequestBody BoardGame boardGame) {
        try {
            Long id = da.addBoardGame(boardGame);
            boardGame.setId(id);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
            logger.log(Level.INFO, "New board game added with id: " + id);
            return ResponseEntity.created(location).body(boardGame);
        } catch (Exception e) {
            logger.log(Level.INFO, "Error occurred while adding board game", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessage("Name already exists."));
        }
    }
}
