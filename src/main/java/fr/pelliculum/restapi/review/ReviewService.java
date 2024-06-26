package fr.pelliculum.restapi.review;


import fr.pelliculum.restapi.answer.AnswerRepository;
import fr.pelliculum.restapi.configuration.handlers.Response;
import fr.pelliculum.restapi.entities.Answer;
import fr.pelliculum.restapi.entities.Review;
import fr.pelliculum.restapi.entities.User;
import fr.pelliculum.restapi.user.UserRepository;
import fr.pelliculum.restapi.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;

    /**
     * Get all reviews for a film
     *
     * @param movieId {@link Long} movieId
     * @return {@link List<Review>} reviews
     */

    public ResponseEntity<Object> getReviewsByMovieId(Long movieId) {
        List<ReviewDTO> reviews = reviewRepository.findReviewDTOsByMovieId(movieId);

        for (ReviewDTO reviewDTO : reviews) {
            // Hypothétique méthode pour récupérer les UserDTO qui ont aimé cette critique
            List<String> likes = userRepository.findUserNamesByReviewIdNative(reviewDTO.getId());
            System.out.println(likes);
            reviewDTO.setLikes(likes); // Assurez-vous que ReviewDTO a une méthode pour définir les likes
            List<String> answers = userRepository.findAnswerByReviewIdNative(reviewDTO.getId());
            reviewDTO.setAnswers(answers);

        }
        return Response.ok("Reviews for movieId: " + movieId, reviews);
    }

    /**
     * Get all reviews for a user
     *
     * @param username {@link String} username
     * @return {@link List<Review>} reviews
     */
    public ResponseEntity<Object> getReviewsByUsername(String username) {
        List<ReviewDTO> reviews = reviewRepository.findReviewDTOsByUsername(username);
        return Response.ok("Reviews for username: " + username, reviews);
    }

    /**
     * Update a review
     *
     * @param reviewId {@link Long} reviewId
     * @param review   {@link Review} review
     * @return {@link Review} review
     */
    public ResponseEntity<Object> updateReview(Long reviewId, Review review) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);

        if (reviewOpt.isEmpty()) {
            return Response.error("Review not found !");
        }

        Review reviewToUpdate = reviewOpt.get();
        reviewToUpdate.setRating(review.getRating());
        reviewToUpdate.setComment(review.getComment());
        reviewToUpdate.setSpoiler(review.isSpoiler());
        reviewRepository.save(reviewToUpdate);
        ReviewDTO reviewDTO = new ReviewDTO(reviewToUpdate);
        return Response.ok("Review successfully updated !", reviewDTO);

    }

    /**
     * Delete a review
     *
     * @param reviewId {@link Long} reviewId
     * @return {@link Review} review
     */
    public ResponseEntity<Object> deleteReview(Long reviewId) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);

        if (reviewOpt.isEmpty()) {
            return Response.error("Review not found !");
        }

        reviewRepository.deleteById(reviewId);
        return Response.ok("Review successfully deleted !");
    }

    /**
     * Like a review
     * param username {@link String} username
     * @param reviewId {@link Long} reviewId
     * @return {@link Review} review
     */

    public ResponseEntity<Object> likeReview(String username, Long reviewId) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);

        if (reviewOpt.isEmpty()) {
            return Response.error("Review not found !");
        }

        Review review = reviewOpt.get();

        if (review.getUser().getUsername().equals(username)) {
            return Response.error("You can't like your own review !");
        }

        User user = userService.findByUsernameOrNotFound(username);
        if (review.getLikes().contains(user)) {
            review.getLikes().remove(user);
            reviewRepository.save(review);
            return Response.ok("Review successfully unliked !");
        }

        review.getLikes().add(user);
        reviewRepository.save(review);
        return Response.ok("Review successfully liked !");
    }

    /**
     * Add an answer to a review
     *
     * @param reviewId {@link Long} reviewId
     * @param answer   {@link Answer} answer
     * @return {@link Answer} answer
     */

    @Transactional
    public ResponseEntity<Object> addAnswerToReview(String username, Long reviewId, Answer answer) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);

        if (reviewOpt.isEmpty()) {
            return Response.error("Review not found !");
        }

        User user = userService.findByUsernameOrNotFound(username);
        answer.setUser(user);

        Review review = reviewOpt.get();
        review.getAnswers().add(answer);
        answerRepository.save(answer);
        reviewRepository.save(review);
        return Response.ok("Answer successfully added to review !", answer);

    }



}
