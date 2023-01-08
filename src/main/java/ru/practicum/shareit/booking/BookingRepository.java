package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerIdOrderByStartDesc(long bookerId, Pageable pageable);

    Page<Booking> findByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            long bookerId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusEqualsOrderByStartDesc(long bookerId, BookingStatus status, Pageable pageable);

    @Query(value = "select * " +
            "from bookings as b join items i on i.id = b.item_id " +
            "where i.owner_id = ?1 " +
            "order by start_date desc ",
            countQuery = "select count(*) " +
                    "from bookings as b join items i on i.id = b.item_id " +
                    "where i.owner_id = ?1 ",
            nativeQuery = true)
    Page<Booking> findAllBookingsForOwner(long ownerId, Pageable pageable);

    @Query(value = "select * " +
            "from bookings as b join items i on i.id = b.item_id " +
            "where i.owner_id = ?1 and b.start_date< ?2 and b.end_date > ?2 " +
            "order by start_date desc ",
            countQuery = "select count(*) " +
                    "from bookings as b join items i on i.id = b.item_id " +
                    "where i.owner_id = ?1 and b.start_date< ?2 and b.end_date > ?2 ",
            nativeQuery = true)
    Page<Booking> findCurrentBookingsForOwner(long ownerId, LocalDateTime time, Pageable pageable);

    @Query(value = "select * " +
            "from bookings as b join items i on i.id = b.item_id " +
            "where i.owner_id = ?1 and b.end_date < ?2 " +
            "order by start_date desc ",
            countQuery = "select count(*) " +
                    "from bookings as b join items i on i.id = b.item_id " +
                    "where i.owner_id = ?1 and b.end_date < ?2 ",
            nativeQuery = true)
    Page<Booking> findPastBookingsForOwner(long ownerId, LocalDateTime time, Pageable pageable);

    @Query(value = "select * " +
            "from bookings as b join items i on i.id = b.item_id " +
            "where i.owner_id = ?1 and b.start_date > ?2 " +
            "order by start_date desc ",
            countQuery = "select count(*) " +
                    "from bookings as b join items i on i.id = b.item_id " +
                    "where i.owner_id = ?1 and b.start_date > ?2 ",
            nativeQuery = true)
    Page<Booking> findFutureBookingsForOwner(long ownerId, LocalDateTime time, Pageable pageable);

    @Query(value = "select * " +
            "from bookings as b join items i on i.id = b.item_id " +
            "where i.owner_id = ?1 and b.status = ?2 " +
            "order by start_date desc ",
            countQuery = "select count(*) " +
                    "from bookings as b join items i on i.id = b.item_id " +
                    "where i.owner_id = ?1 and b.status = ?2 ",
            nativeQuery = true)
    Page<Booking> findStatusBookingsForOwner(long ownerId, String status, Pageable pageable);

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

