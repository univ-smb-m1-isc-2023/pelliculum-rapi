package fr.pelliculum.restapi.list;

import fr.pelliculum.restapi.configuration.exceptions.ListNotFoundException;
import fr.pelliculum.restapi.configuration.exceptions.UserNotFoundException;
import fr.pelliculum.restapi.configuration.handlers.Response;
import fr.pelliculum.restapi.entities.List;
import fr.pelliculum.restapi.entities.User;
import fr.pelliculum.restapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListService {

    private final ListRepository listRepository;
    private final UserRepository userRepository;


    /**
     * Get a list by id or throw an exception (404)
     * @param id {@link Long} id
     * @return {@link List} list
     */
    public List findListByIdOrNotFound(Long id) {
        return listRepository.findById(id)
                .orElseThrow(() -> new ListNotFoundException("List not found with id: " + id));
    }


    /**
     * Get a list by id
     * @param id {@link Long} id
     * @return {@link ResponseEntity} list
     */
    @Transactional
    public ResponseEntity<Object> getListById(Long id) {
        return Response.ok("List successfully founded !", findListByIdOrNotFound(id));
    }

    /**
     * Get lists
     * @param isPublic {@link Boolean} isPublic
     * @return {@link ResponseEntity} lists
     */
    @Transactional
    public ResponseEntity<Object> getLists(Boolean isPublic) {
        return Response.ok("Lists successfully founded !", listRepository.findPublic(isPublic));
    }

    /**
     * Get lists by username
     * @param username {@link String} username
     * @return {@link ResponseEntity} lists
     */
    @Transactional
    public ResponseEntity<Object> getListsByUsername(String username, boolean isPublic) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + username));
        if (user == null) return Response.notFound("User not found with email: " + username);
        return Response.ok("Lists successfully founded !", listRepository.findByUserId(isPublic, user));
    }

    /**
     * Delete a list by id
     * @param id {@link Long} id
     * @return {@link ResponseEntity} message
     */
    public ResponseEntity<Object> deleteList(Long id) {
        List list = findListByIdOrNotFound(id);
        listRepository.deleteById(id);
        return Response.ok("List successfully deleted !", list);
    }



    /**
     * Create a list
     * @param name {@link String} name
     * @param description {@link String} description
     * @param isPublic {@link Boolean} isPublic
     * @param username {@link String} email
     * @return {@link ResponseEntity} list
     */
    @Transactional
    public ResponseEntity<Object> createList(String name, String description, Boolean isPublic, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + username));
        if (user == null) return Response.notFound("User not found with email: " + username);
        List list = new List();
        list.setName(name);
        list.setDescription(description);
        list.setIsPublic(isPublic);
        list.setUser(user);
        listRepository.save(list);
        return Response.ok("List successfully created !", list);
    }

    /**
     * Add a movie to a list
     * @param id {@link Long} id
     * @param movieId {@link Long} movieId
     * @return {@link ResponseEntity} message
     */
    public ResponseEntity<Object> addMovieToList(Long id, Long movieId) {
        List list = findListByIdOrNotFound(id);
        if(list.getMovies().contains(movieId)) return Response.badRequest("Movie already in list !");
        list.getMovies().add(movieId);
        listRepository.save(list);
        return Response.ok("Movie successfully added to list !");
    }

    /**
     * Remove a movie from a list
     * @param id {@link Long} id
     * @param movieId {@link Long} movieId
     * @return {@link ResponseEntity} message
     */
    public ResponseEntity<Object> removeMovieFromList(Long id, Long movieId) {
        List list = findListByIdOrNotFound(id);
        if (!list.getMovies().contains(movieId)) return Response.badRequest("Movie not in list !");
        list.getMovies().remove(movieId);
        listRepository.save(list);
        return Response.ok("Movie successfully removed from list !");
    }

}
