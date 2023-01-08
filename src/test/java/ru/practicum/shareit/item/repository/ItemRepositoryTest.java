package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void createItem() {
        Item item1 = new Item(0L, "name1", "desc1", true, 1L, 10L);
        Item item2 = new Item(0L, "name2", "desc2", true, 1L, 10L);
        User user1 = new User(0L, "name1", "email1@ru");

        itemRepository.save(item1);
        itemRepository.save(item2);
        userRepository.save(user1);
    }

    @DirtiesContext
    @Test
    void findAllByOwnerIdIsOrderById_whenItemNotFound_thenReturnEmptyCollection() {
        long userId = 3L;

        Collection<Item> result = itemRepository.findAllByOwnerIdIsOrderById(
                userId, Pageable.ofSize(10)).getContent();

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void findAllByOwnerIdIsOrderById_whenItemFound_thenReturnCollection() {
        long userId = 1L;

        Collection<Item> result = itemRepository.findAllByOwnerIdIsOrderById(
                userId, Pageable.ofSize(10)).getContent();

        assertEquals(2, result.size());
        assertEquals("name1", new ArrayList<>(result).get(0).getName());
        assertEquals("name2", new ArrayList<>(result).get(1).getName());
    }

    @DirtiesContext
    @Test
    void findAllItemByText_whenItemForNameNotFound_thenReturnEmptyCollection() {
        String name = "pump";

        Collection<Item> result = itemRepository.findAllItemByText(name, Pageable.ofSize(10)).getContent();

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void findAllItemByText_whenItemName1Found_thenReturnCollectionSizeOne() {
        String name = "name1";

        Collection<Item> result = itemRepository.findAllItemByText(name, Pageable.ofSize(10)).getContent();

        assertEquals(1, result.size());
        assertEquals("name1", new ArrayList<>(result).get(0).getName());
    }

    @DirtiesContext
    @Test
    void findAllItemByText_whenItemNameFound_thenReturnCollectionSizeTwo() {
        String name = "namE";

        Collection<Item> result = itemRepository.findAllItemByText(name, Pageable.ofSize(10)).getContent();

        assertEquals(2, result.size());
        assertEquals("name1", new ArrayList<>(result).get(0).getName());
        assertEquals("name2", new ArrayList<>(result).get(1).getName());
    }

    @DirtiesContext
    @Test
    void findAllItemByText_whenItemDescOneFound_thenReturnCollectionSizeOne() {
        String name = "desc1";

        Collection<Item> result = itemRepository.findAllItemByText(name, Pageable.ofSize(10)).getContent();

        assertEquals(1, result.size());
        assertEquals("desc1", new ArrayList<>(result).get(0).getDescription());
    }

    @DirtiesContext
    @Test
    void findAllItemByText_whenItemDescFound_thenReturnCollectionSizeTwo() {
        String name = "desC";

        Collection<Item> result = itemRepository.findAllItemByText(name, Pageable.ofSize(10)).getContent();

        assertEquals(2, result.size());
        assertEquals("desc1", new ArrayList<>(result).get(0).getDescription());
        assertEquals("desc2", new ArrayList<>(result).get(1).getDescription());
    }

    @DirtiesContext
    @Test
    void findAllByRequestId_whenItemNotFound_thenReturnEmptyCollection() {
        long requestId = 2L;

        Collection<Item> result = itemRepository.findAllByRequestId(requestId);

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void findAllByRequestId_whenItemFound_thenReturnCollectionSizeTwo() {
        long requestId = 10L;

        Collection<Item> result = itemRepository.findAllByRequestId(requestId);

        assertEquals(2, result.size());
    }

}