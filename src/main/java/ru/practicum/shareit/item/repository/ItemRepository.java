package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwnerIdIsOrderById(Long userId);

    @Query("select new Item(it.id, it.name, it.description, it.available, it.ownerId, it.requestId) " +
            "from Item as it " +
            "where it.available = true " +
            "and ( upper(it.name) like upper(concat('%', ?1, '%')) " +
            "or upper(it.description) like upper(concat('%', ?1, '%'))) ")
    Collection<Item> findAllItemByText(String text);

}