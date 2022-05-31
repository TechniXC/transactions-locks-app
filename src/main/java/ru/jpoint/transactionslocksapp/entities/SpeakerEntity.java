package ru.jpoint.transactionslocksapp.entities;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "speakers")
public class SpeakerEntity {

    @Id
    private Long id;

    @Column(name = "firstname")
    private String FirstName;
    @Column(name = "lastname")
    private String LastName;

    @Column(name = "talkname")
    private String talkName;

    private int likes;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime created;

//    @Version
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updated;

}
