package com.example.crosspayment.repository;


import com.example.crosspayment.model.Payment;
import com.example.crosspayment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *  Payment repository:
 *      Handles all the database operations related to Payment entities.
 *
 *      SpringJPA will include the following methods by default from JpaRepository:
 *      - save(S entity): Saves a given entity.
 *      - findById(ID id): Retrieves an entity by its id.  -- this will reference transactionId
 *      - findAll(): Returns all instances of the type.
 *      - count(): Returns the number of entities.
 *      - existsById(ID id): Checks if an entity with the given id exists.
 *
 *
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     *
     *  Select * from payments where status = :status
     * @param status
     * @return List of payments with the given status
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     *
     * Select * from payments where sender = :sender
     * @param sender
     * @return List of payments with the given sender
     */
    List<Payment> findBySender(String sender);

    /**
     *
     * Select * from payments where receiver = :receiver
     * @param receiver
     * @return List of payments with the given receiver
     */
    List<Payment> findByReceiver(String receiver);


}
