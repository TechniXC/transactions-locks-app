CREATE TABLE jpoint.speakers
(
    id         SERIAL NOT NULL,
    firstname  varchar NOT NULL,
    lastname   varchar NOT NULL,
    talkname   varchar NOT NULL,
    likes      int8 NULL,
    created    timestamp NULL,
    updated    timestamp NULL,

    CONSTRAINT speakers_pk PRIMARY KEY (id),
    CONSTRAINT talkname_unique UNIQUE (talkname)
);