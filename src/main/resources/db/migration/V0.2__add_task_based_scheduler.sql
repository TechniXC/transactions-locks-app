CREATE TABLE jpoint.sheduled_tasks
(
    id        SERIAL  NOT NULL,
    task_type varchar NOT NULL,
    task_data jsonb   NOT NULL,
    completed boolean NOT NULL DEFAULT false,

    CONSTRAINT sheduled_tasks_pk PRIMARY KEY (id)
);

CREATE TABLE jpoint.shedlock
(
    name       VARCHAR(64),
    lock_until TIMESTAMP(3) NULL,
    locked_at  TIMESTAMP(3) NULL,
    locked_by  VARCHAR(255),

    CONSTRAINT shedlock_pk PRIMARY KEY (name)
);