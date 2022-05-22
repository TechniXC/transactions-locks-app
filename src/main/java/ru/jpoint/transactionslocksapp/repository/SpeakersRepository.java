package ru.jpoint.transactionslocksapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import ru.jpoint.transactionslocksapp.entities.SpeakerEntity;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Optional;

@Repository
public interface SpeakersRepository extends JpaRepository<SpeakerEntity, Long> {

//    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
//        @Lock(LockModeType.PESSIMISTIC_WRITE)
//        @QueryHints({
//                @QueryHint(name = "javax.persistence.query.timeout", value = "2000"),
//    //            @QueryHint(name = "javax.persistence.query.timeout", value = "-2")
//        })
    Optional<SpeakerEntity> findByTalkName(String talkName);

//        @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
//        @Lock(LockModeType.PESSIMISTIC_WRITE)
//        @QueryHints({
//                @QueryHint(name = "javax.persistence.query.timeout", value = "2000"),
//    //            @QueryHint(name = "javax.persistence.query.timeout", value = "-2")
//        })
    @Override
    Optional<SpeakerEntity> findById(Long aLong);
}
