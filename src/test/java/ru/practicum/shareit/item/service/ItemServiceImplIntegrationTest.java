package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    private final ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void createdUnite() {
        userRepository.save(new User(0L, "owner", "owner@ru"));
        userRepository.save(new User(0L, "user", "user@ru"));
    }

    @DirtiesContext
    @Test
    void getAllItems_whenBdIsEmpty_thenReturnEmptyList() {

        Collection<ItemDtoForOwner> result = itemService.getAllItems(1L, 0, 10);

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void getAllItems_whenBdHasItem_thenReturnList() {
        Item item1 = new Item(0L, "item1", "desc1", true, 1L, null);
        itemRepository.save(item1);

        Collection<ItemDtoForOwner> result = itemService.getAllItems(1L, 0, 10);

        assertEquals(1, result.size());
        assertEquals(item1.getName(), new ArrayList<>(result).get(0).getName());
    }

    @DirtiesContext
    @Test
    void getItemById_whenUserNotFound_thenObjectNotFoundException() {
        Item item1 = new Item(0L, "item1", "desc1", true, 1L, null);
        itemRepository.save(item1);

        assertThrows(ObjectNotFoundException.class, () -> itemService.getItemById(10L, 1L));
    }

    @DirtiesContext
    @Test
    void getItemById_whenItemNotFound_thenObjectNotFoundException() {
        Item item1 = new Item(0L, "item1", "desc1", true, 1L, null);
        itemRepository.save(item1);

        assertThrows(ObjectNotFoundException.class, () -> itemService.getItemById(1L, 10L));
    }

    @DirtiesContext
    @Test
    void getItemById_whenOwnerRequest_thenReturnWithBookings() {
        Item item1 = new Item(0L, "item1", "desc1", true, 1L, null);
        itemRepository.save(item1);

        ItemDtoAbstract result = itemService.getItemById(1L, 1L);

        assertEquals(ItemDtoForOwner.class, result.getClass());
        assertTrue(result.toString().contains("name=item1"));
    }

    @DirtiesContext
    @Test
    void getItemById_whenUserRequest_thenReturnWithoutBookings() {
        Item item1 = new Item(0L, "item1", "desc1", true, 1L, null);
        itemRepository.save(item1);

        ItemDtoAbstract result = itemService.getItemById(2L, 1L);

        assertEquals(ItemDtoForBooker.class, result.getClass());
        assertTrue(result.toString().contains("name=item1"));
    }

    @DirtiesContext
    @Test
    void createItem_whenInvoke_thenReturnNewSaveItem() {
        long ownerId = 1L;
        ItemDto itemForSave = new ItemDto(
                0L,
                "newItem",
                "newDesk",
                true,
                1L,
                null);

        ItemDto result = itemService.createItem(ownerId, itemForSave);

        assertEquals(1, result.getId());
        assertEquals(itemForSave.getName(), result.getName());
        assertEquals(itemForSave.getDescription(), result.getDescription());
        assertEquals(itemForSave.getOwnerId(), result.getOwnerId());
        assertEquals(itemForSave.getAvailable(), result.getAvailable());
    }


    @DirtiesContext
    @Test
    void updateItem_whenInvokeByNotOwner_thenObjectNotFoundException() {
        ItemDto itemForUpdate =
                new ItemDto(0L, "updateItem", "updateDesk", false, 1L, null);
        Item oldItem = new Item(0L, "itemOld", "descOld", true, 1L, null);
        itemRepository.save(oldItem);

        assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(1L, 2L, itemForUpdate));
    }

    @DirtiesContext
    @Test
    void updateItem_whenInvokeByOwner_thenReturnUpdateItem() {
        ItemDto itemForUpdate =
                new ItemDto(0L, "updateItem", "updateDesk", false, 1L, null);
        Item oldItem = new Item(0L, "itemOld", "descOld", true, 1L, null);
        itemRepository.save(oldItem);

        ItemDto result = itemService.updateItem(1L, 1L, itemForUpdate);

        assertEquals(1L, result.getId());
        assertEquals(itemForUpdate.getName(), result.getName());
        assertEquals(itemForUpdate.getDescription(), result.getDescription());
        assertEquals(itemForUpdate.getAvailable(), result.getAvailable());
    }

    @DirtiesContext
    @Test
    void searchItemByName_whenNotSearch_thenReturnEmptyList() {
        String text = "text";
        Item item1 = new Item(0L, "item1", "desc1", true, 1L, null);
        Item item2 = new Item(0L, "item2", "desc2", false, 1L, null);
        itemRepository.save(item1);
        itemRepository.save(item2);

        Collection<ItemDtoForBooker> result = itemService.searchItemByName(2L, text, 0, 10);

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void searchItemByName_whenSearchByNameIsItem1_thenReturnItem1() {
        String text = "item1";
        Item item1 = new Item(0L, "item1", "desc1", true, 1L, null);
        Item item2 = new Item(0L, "item2", "desc2", true, 1L, null);
        itemRepository.save(item1);
        itemRepository.save(item2);

        Collection<ItemDtoForBooker> result = itemService.searchItemByName(2L, text, 0, 10);

        assertEquals(1, result.size());
        assertEquals(item1.getName(), new ArrayList<>(result).get(0).getName());
    }

    @DirtiesContext
    @Test
    void searchItemByName_whenSearchByNameIsItem_thenReturnBothItem() {
        String text = "item";
        Item item1 = new Item(0L, "item1", "desc1", true, 1L, null);
        Item item2 = new Item(0L, "item2", "desc2", true, 1L, null);
        itemRepository.save(item1);
        itemRepository.save(item2);

        Collection<ItemDtoForBooker> result = itemService.searchItemByName(2L, text, 0, 10);

        assertEquals(2, result.size());
        assertEquals(item1.getName(), new ArrayList<>(result).get(0).getName());
        assertEquals(item2.getName(), new ArrayList<>(result).get(1).getName());
    }

    @DirtiesContext
    @Test
    void searchItemByName_whenSearchByNameIsItemButItem1NotAvailable_thenReturnItem2() {
        String text = "itEm";
        Item item1 = new Item(0L, "item1", "desc1", false, 1L, null);
        Item item2 = new Item(0L, "item2", "desc2", true, 1L, null);
        itemRepository.save(item1);
        itemRepository.save(item2);

        Collection<ItemDtoForBooker> result = itemService.searchItemByName(2L, text, 0, 10);

        assertEquals(1, result.size());
        assertEquals(item2.getName(), new ArrayList<>(result).get(0).getName());
    }

    @DirtiesContext
    @Test
    void searchItemByName_whenSearchByDescription_thenReturnBothItem() {
        String text = "deSc";
        Item item1 = new Item(0L, "item1", "desc1", true, 1L, null);
        Item item2 = new Item(0L, "item2", "desc2", true, 1L, null);
        itemRepository.save(item1);
        itemRepository.save(item2);

        Collection<ItemDtoForBooker> result = itemService.searchItemByName(2L, text, 0, 10);

        assertEquals(2, result.size());
        assertEquals(item1.getName(), new ArrayList<>(result).get(0).getName());
        assertEquals(item2.getName(), new ArrayList<>(result).get(1).getName());
    }

    @DirtiesContext
    @Test
    void deleteItem_whenInvoke_thenDeleteItemFromBd() {
        long userId = 1L;
        long itemId = 1L;
        Item item1 = new Item(0L, "item1", "desc1", true, 1L, null);
        itemRepository.save(item1);

        assertEquals(1, itemRepository.findAll().size());

        itemService.deleteItem(itemId, userId);

        assertTrue(itemRepository.findAll().isEmpty());
    }

    @DirtiesContext
    @Test
    void createComment() {
        CommentDtoIn newComment = new CommentDtoIn("newComment");
        Item item1 = new Item(0L, "item1", "desc1", true, 1L, null);
        itemRepository.save(item1);
        bookingRepository.save(
                new Booking(
                        0L,
                        LocalDateTime.now().minusDays(10),
                        LocalDateTime.now(),
                        1L,
                        2L,
                        BookingStatus.APPROVED));

        CommentDtoOut result = itemService.createComment(item1.getId(), 2L, newComment);

        assertEquals(1L, result.getId());
        assertEquals(newComment.getText(), result.getText());
        assertEquals("user", result.getAuthorName());
    }

}