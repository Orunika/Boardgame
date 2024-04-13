package com.javaproject.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.javaproject.beans.BoardGame;
import com.javaproject.beans.Review;
import com.javaproject.database.DatabaseAccess;

@Controller
public class HomeController {

    private static final Logger logger = Logger.getLogger(HomeController.class.getName());

    @Autowired
    DatabaseAccess da;

    @Autowired
    @Lazy
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JdbcUserDetailsManager jdbcUserDetailsManager;

    @GetMapping("/newUser")
    public String newUser(Model model) {
        logger.log(Level.INFO, "Rendering new user page");
        List<String> authorities = da.getAuthorities();
        model.addAttribute("authorities", authorities);
        return "new-user";
    }

    @PostMapping("/addUser")
    public String addUser(@RequestParam String userName, @RequestParam String password,
                          @RequestParam String[] authorities, Model model, RedirectAttributes redirectAttrs) {
        logger.log(Level.INFO, "Adding new user: " + userName);
        List<GrantedAuthority> authorityList = new ArrayList<>();

        for (String authority : authorities) {
            authorityList.add(new SimpleGrantedAuthority(authority));
        }
        String encodedPassword = passwordEncoder.encode(password);

        // check existing user
        if (jdbcUserDetailsManager.userExists(userName)) {
            logger.log(Level.INFO, "User name already exists: " + userName);
            model.addAttribute("errorMsg", "User name already Exists. Try a different user name.");
            model.addAttribute("authorities", authorityList);
            return "new-user";
        } else {
            User user = new User(userName, encodedPassword, authorityList);
            jdbcUserDetailsManager.createUser(user);
            logger.log(Level.INFO, "New user added: " + userName);
            redirectAttrs.addFlashAttribute("userAddedMsg", "User successfully added!");
            return "redirect:/";
        }
    }

    @GetMapping("/")
    public String goHome(Model model) {
        logger.log(Level.INFO, "Rendering home page");
        List<BoardGame> boardgames = da.getBoardGames();
        model.addAttribute("boardgames", boardgames);
        return "index";
    }

    @GetMapping("/{id}")
    public String getBoardgameDetail(@PathVariable Long id, Model model) {
        logger.log(Level.INFO, "Rendering board game detail for id: " + id);
        model.addAttribute("boardgame", da.getBoardGame(id));
        return "boardgame";
    }

    @GetMapping("/{id}/reviews")
    public String getReviews(@PathVariable Long id, Model model) {
        logger.log(Level.INFO, "Rendering reviews for board game id: " + id);
        model.addAttribute("boardgame", da.getBoardGame(id));
        model.addAttribute("reviews", da.getReviews(id));
        return "review";
    }

    @GetMapping("/secured/addReview/{id}")
    public String addReview(@PathVariable Long id, Model model) {
        logger.log(Level.INFO, "Rendering page to add review for board game id: " + id);
        model.addAttribute("boardgame", da.getBoardGame(id));
        model.addAttribute("review", new Review());
        return "secured/addReview";
    }

    // edit the review
    @GetMapping("/{gameId}/reviews/{id}")
    public String editReview(@PathVariable Long gameId, @PathVariable Long id, Model model) {
        logger.log(Level.INFO, "Rendering page to edit review id: " + id + " for board game id: " + gameId);
        Review review = da.getReview(id);
        model.addAttribute("review", review);
        model.addAttribute("boardgame", da.getBoardGame(gameId));
        return "secured/addReview";
    }

    @GetMapping("/secured/addBoardGame")
    public String addBoardGame(Model model) {
        logger.log(Level.INFO, "Rendering page to add new board game");
        model.addAttribute("boardgame", new BoardGame());
        return "secured/addBoardGame";
    }

    @PostMapping("/boardgameAdded")
    public String boardgameAdded(@ModelAttribute BoardGame boardgame) {
        logger.log(Level.INFO, "Adding new board game: " + boardgame.getName());
        Long returnValue = da.addBoardGame(boardgame);
        logger.log(Level.INFO, "New board game added with id: " + returnValue);
        return "redirect:/";
    }

    @PostMapping("/reviewAdded")
    public String reviewAdded(@ModelAttribute Review review) {
        logger.log(Level.INFO, "Adding new review for board game id: " + review.getGameId());
        int returnValue;
        // if id exists, edit
        if (review.getId() != null) {
            returnValue = da.editReview(review);
        } else {
            // if id not exists, add
            returnValue = da.addReview(review);
        }
        logger.log(Level.INFO, "Review added with return value: " + returnValue);
        return "redirect:/" + review.getGameId() + "/reviews";
    }

    @GetMapping("/deleteReview/{id}")
    public String deleteReview(@PathVariable Long id) {
        logger.log(Level.INFO, "Deleting review with id: " + id);
        Long gameId = da.getReview(id).getGameId();
        int returnValue = da.deleteReview(id);
        logger.log(Level.INFO, "Review deleted with return value: " + returnValue);
        return "redirect:/" + gameId + "/reviews";
    }

    @GetMapping("/user")
    public String goToUserSecured() {
        logger.log(Level.INFO, "Rendering user secured page");
        return "secured/user/index";
    }

    @GetMapping("/manager")
    public String goToManagerSecured() {
        logger.log(Level.INFO, "Rendering manager secured page");
        return "secured/manager/index";
    }

    @GetMapping("/secured")
    public String goToSecured() {
        logger.log(Level.INFO, "Rendering secured gateway page");
        return "secured/gateway";
    }

    @GetMapping("/login")
    public String goToLogin() {
        logger.log(Level.INFO, "Rendering login page");
        return "login";
    }

    @GetMapping("/permission-denied")
    public String goToDenied() {
        logger.log(Level.INFO, "Rendering permission denied page");
        return "error/permission-denied";
    }
}
