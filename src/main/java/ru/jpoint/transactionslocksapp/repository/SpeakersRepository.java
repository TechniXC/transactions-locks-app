package ru.jpoint.transactionslocksapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.jpoint.transactionslocksapp.entities.SpeakerEntity;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Optional;

@Repository
public interface SpeakersRepository extends JpaRepository<SpeakerEntity, Long> {

    //<editor-fold desc="Optimistic Lock">
//    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    //</editor-fold>
    //<editor-fold desc="Pessimistic Lock with hint">
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @QueryHints({
//            @QueryHint(name = "javax.persistence.query.timeout", value = "2000"),
//            //            @QueryHint(name = "javax.persistence.query.timeout", value = "-2")
//    })
    //</editor-fold>
    Optional<SpeakerEntity> findByTalkName(String talkName);

    //<editor-fold desc="Optimistic Lock">
//    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    //</editor-fold>
    //<editor-fold desc="Pessimistic Lock with hint">
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @QueryHints({
//            @QueryHint(name = "javax.persistence.query.timeout", value = "2000"),
//            //            @QueryHint(name = "javax.persistence.query.timeout", value = "-2")
//    })
    //</editor-fold>
    @Override
    Optional<SpeakerEntity> findById(Long aLong);
}
