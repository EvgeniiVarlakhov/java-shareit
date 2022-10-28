package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findByBookerIdOrderByStartDesc(long bookerId);

    Collection<Booking> findByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime start);

    Collection<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long bookerId, LocalDateTime start, LocalDateTime end);

    Collection<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime start);

    Collection<Booking> findByBookerIdAndStatusEqualsOrderByStartDesc(long bookerId, BookingStatus status);

    @Query(value = "select * " +
            "from bookings as b join items i on i.id = b.item_id " +
            "where i.owner_id = ?1 " +
            "order by start_date desc ", nativeQuery = true)
    Collection<Booking> findAllBookingsForOwner(long ownerId);

    @Query(value = "select * " +
            "from bookings as b join items i on i.id = b.item_id " +
            "where i.owner_id = ?1 and b.start_date< ?2 and b.end_date > ?2 " +
            "order by start_date desc ", nativeQuery = true)
    Collection<Booking> findCurrentBookingsForOwner(long ownerId, LocalDateTime time);

    @Query(value = "select * " +
            "from bookings as b join items i on i.id = b.item_id " +
            "where i.owner_id = ?1 and b.end_date < ?2 " +
            "order by start_date desc ", nativeQuery = true)
    Collection<Booking> findPastBookingsForOwner(long ownerId, LocalDateTime time);

    @Query(value = "select * " +
            "from bookings as b join items i on i.id = b.item_id " +
            "where i.owner_id = ?1 and b.start_date > ?2 " +
            "order by start_date desc ", nativeQuery = true)
    Collection<Booking> findFutureBookingsForOwner(long ownerId, LocalDateTime time);

    @Query(value = "select * " +
            "from bookings as b join items i on i.id = b.item_id " +
            "where i.owner_id = ?1 and b.status = ?2 " +
            "order by start_date desc ", nativeQuery = true)
    Collection<Booking> findStatusBookingsForOwner(long ownerId, String status);

    @Query(value = "select * " +
            "from bookings as b join items i on i.id = b.item_id " +
            "where i.id = ?1 and b.end_date < ?2 " +
            "order by b.end_date desc " +
            "limit 1 ", nativeQuery = true)
    Optional<Booking> findLastBookingByItem(long itemId, LocalDateTime time);

    @Query(value = "select * " +
            "from bookings as b join items i on i.id = b.item_id " +
            "where i.id = ?1 and b.start_date > ?2 " +
            "order by b.end_date desc " +
            "limit 1", nativeQuery = true)
    Optional<Booking> findNextBookingByItem(long itemId, LocalDateTime time);

    Optional<Booking> findBookingByItemIdAndBookerIdAndEndIsBefore(long itemId, long userId, LocalDateTime time);

}

