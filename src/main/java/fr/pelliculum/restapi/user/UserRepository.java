package fr.pelliculum.restapi.user;

import fr.pelliculum.restapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{

    Optional<User> findByUsername(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    @Query("SELECT u FROM User u JOIN u.follows f WHERE f.username = :username")
    List<User> findFollowersByUsername(String username);

    @Query("SELECT u FROM User u JOIN u.follows f WHERE f.username = :username")
    List<User> findFollowsByUsername(String username);

    @Query(value = "SELECT u.username FROM users u WHERE id IN (SELECT likes_id FROM review_likes WHERE review_id = :reviewId)", nativeQuery = true)
    List<String> findUserNamesByReviewIdNative(@Param("reviewId") Long reviewId);

    @Query(value = "SELECT answers_id FROM review_answers WHERE review_id = :reviewId", nativeQuery = true)
    List<String> findAnswerByReviewIdNative(@Param("reviewId") Long reviewId);

    @Query(value = "SELECT u.username," +
            "(SELECT COUNT(*) FROM users_follows WHERE user_id = f.follows_id) AS followsCount, " +
            "(SELECT COUNT(*) FROM users_follows WHERE follows_id = f.follows_id) AS followersCount " +
            "FROM users u " +
            "INNER JOIN users_follows f ON u.id = f.follows_id " +
            "WHERE f.user_id = (SELECT id FROM users WHERE username = :username)",
            nativeQuery = true)
    List<Object[]> findFollowsDetailsByUsernameNative(@Param("username") String username);

    @Query(value = "SELECT u.username," +
            "(SELECT COUNT(*) FROM users_follows WHERE user_id = f.follows_id) AS followsCount, " +
            "(SELECT COUNT(*) FROM users_follows WHERE follows_id = f.follows_id) AS followersCount, " +
            "EXISTS(SELECT 1 FROM users_follows uf JOIN users us ON us.id = uf.user_id WHERE uf.follows_id = f.user_id AND us.username = :username) " +
            "FROM users u INNER JOIN users_follows f ON u.id = f.user_id " +
            "WHERE f.follows_id = (SELECT id FROM users WHERE username = :username)",
            nativeQuery = true)
    List<Object[]> findFollowersDetailsByUsernameNative(@Param("username") String username);

    @Query(value = "SELECT " +
            "(SELECT COUNT(*) FROM users_follows WHERE user_id = u.id) AS followingCount, " +
            "(SELECT COUNT(*) FROM users_follows WHERE follows_id = u.id) AS followersCount " +
            "FROM users u " +
            "WHERE u.username = :username",
            nativeQuery = true)
    List<Object[]> findFollowingAndFollowersByUsername(@Param("username") String username);

    Boolean existsByEmail(String email);



    @Query(value = "SELECT COUNT(*) FROM review_likes WHERE likes_id = :userId", nativeQuery = true)
    Long countReviewLikesById(Long userId);
}
