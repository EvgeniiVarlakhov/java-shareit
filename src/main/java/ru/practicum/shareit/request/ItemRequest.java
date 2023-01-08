package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@ToString
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String description;

    @Column(name = "requestor_id", nullable = false)
    private long requestorId;

    @Column(name = "created_request", nullable = false)
    private LocalDateTime created;
}
