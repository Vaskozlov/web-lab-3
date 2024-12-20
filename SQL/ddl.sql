CREATE TABLE IF NOT EXISTS CHECKS
(
    ID                BIGSERIAL,
    HTTP_SESSION_ID   VARCHAR(128),
    X                 DOUBLE PRECISION,
    Y                 DOUBLE PRECISION,
    R                 DOUBLE PRECISION,
    IS_IN_AREA        BOOLEAN,
    EXECUTION_TIME_NS BIGINT
);


INSERT INTO CHECKS
(HTTP_SESSION_ID,
 X,
 Y,
 R,
 IS_IN_AREA,
 EXECUTION_TIME_NS)
VALUES (?, ?, ?, ?, ?, ?)
RETURNING ID;