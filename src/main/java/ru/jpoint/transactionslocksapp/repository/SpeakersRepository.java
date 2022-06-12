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

    Optional<SpeakerEntity> findByTalkName(String talkName);

}
