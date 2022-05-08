package ru.jpoint.transactionslocksapp.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sheduled_tasks")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ScheduledTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_type")
    private String taskType;

    @Column(name = "task_data")
    @Type(type = "jsonb")
    private Object taskData;

    private boolean completed;


}
