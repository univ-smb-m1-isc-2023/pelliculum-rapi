package fr.pelliculum.restapi.review;

import fr.pelliculum.restapi.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUser_IdAndMovieId(Integer userId, Long movieId);

    @Query("SELECT new fr.pelliculum.restapi.review.ReviewDTO(r.id, r.comment, r.rating, r.movieId, r.user, r.createdAt, r.spoiler) FROM Review r WHERE r.movieId = :movieId")
    List<ReviewDTO> findReviewDTOsByMovieId(Long movieId);


    @Query("SELECT new fr.pelliculum.restapi.review.ReviewDTO(r.id, r.comment, r.rating, r.movieId, r.user, r.createdAt, r.spoiler) FROM Review r WHERE r.user.username = :username")
    List<ReviewDTO> findReviewDTOsByUsername(String username);


    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId")
    Long countByUserId(Long userId);






}
