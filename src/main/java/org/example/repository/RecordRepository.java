package org.example.repository;

import org.example.entity.Record;
import org.example.entity.RecordStatus;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RecordRepository extends JpaRepository<Record, Integer> {
    @Modifying
    @Query("UPDATE Record SET status = :status WHERE id = :id")
    void update(int id, @Param("status") RecordStatus newStatus);

    List<Record> findAllByStatus(RecordStatus status);

    List<Record> findAllByStatusAndTitleContainsOrderByIdDesc(RecordStatus status, String titlePart);

    List<Record> findAllByUserOrderByIdAsc(User user);

}
