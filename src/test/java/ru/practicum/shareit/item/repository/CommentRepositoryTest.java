package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void creatEntity() {
        Comment comment = new Comment();
        comment.setText("text");
        comment.setItemId(1L);
        comment.setAuthorID(1L);
        comment.setCreatedTime(LocalDateTime.now());
        Item item1 = new Item(0L, "name", "desc", true, 1L, null);
        Item item2 = new Item(0L, "name2", "desc2", true, 1L, null);
        User user = new User(0L, "name", "email@ru");

        itemRepository.save(item1);
        itemRepository.save(item2);
        userRepository.save(user);
        commentRepository.save(comment);

    }

    @DirtiesContext
    @Test
    void findAllByItemIdIs_whenCommentNotFound_thenReturnEmptyCollection() {
        long itemId = 2L;

        Collection<Comment> result = commentRepository.findAllByItemId(itemId);

        assertTrue(result.isEmpty());
    }


    @DirtiesContext
    @Test
    void findAllByItemIdIs_whenCommentFound_thenReturnCollection() {
        long itemId = 1L;

        Collection<Comment> result = commentRepository.findAllByItemId(itemId);

        assertEquals(1, result.size());
    }

}