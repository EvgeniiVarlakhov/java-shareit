package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Collection<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(long requestorId);

    Optional<ItemRequest> findItemRequestById(long requestId);

    @Query(value = "select * " +
            "from requests " +
            "where requestor_id != ?1 " +
            "order by created_request desc", nativeQuery = true)
    Page<ItemRequest> findAllRequests (long ownerId, Pageable pageable);

}
