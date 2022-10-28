package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@ToString
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column (name = "start_date")
    private LocalDateTime start;

    @Column (name = "end_date")
    private LocalDateTime end;

    @Column (name = "item_id")
    private long itemId;

    @Column (name = "booker_id")
    private long bookerId;

    @Column (name = "status", columnDefinition = "enum('APPROVED','REJECTED','CANCELED','WAITING')" )
    @Enumerated (EnumType.STRING)
    private BookingStatus status;
}
