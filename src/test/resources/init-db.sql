CREATE TABLE tasks
(
    id          INT     PRIMARY KEY,
    title       VARCHAR NOT NULL,
    type        VARCHAR,
    description VARCHAR,
    priority    VARCHAR,
    status      VARCHAR,
    date        DATE,
    due_date    DATE
);

CREATE TABLE audit_task
(
    id             SERIAL  PRIMARY KEY,
    id_task        INT     REFERENCES tasks(id),
    action         VARCHAR,
    task_title     VARCHAR,
    execution_date DATE
);

