-- V1 : données de référence (listes du livre), partagées par tous les personnages.

CREATE TABLE genre (
    genre_code  varchar(10) NOT NULL,
    genre_name  varchar(50) NOT NULL,
    CONSTRAINT pk_genre PRIMARY KEY (genre_code),
    CONSTRAINT uq_genre_name UNIQUE (genre_name)
);

CREATE TABLE attribute (
    attribute_id    bigint       GENERATED ALWAYS AS IDENTITY,
    genre_code      varchar(10)  NOT NULL,
    attribute_name  varchar(40)  NOT NULL,
    attribute_desc  varchar(500),
    is_extranormal  boolean      NOT NULL DEFAULT false,
    display_order   smallint     NOT NULL,
    CONSTRAINT pk_attribute PRIMARY KEY (attribute_id),
    CONSTRAINT fk_attribute_genre FOREIGN KEY (genre_code) REFERENCES genre (genre_code),
    CONSTRAINT uq_attribute_genre_name UNIQUE (genre_code, attribute_name)
);

CREATE TABLE skill (
    skill_id             bigint       GENERATED ALWAYS AS IDENTITY,
    attribute_id         bigint       NOT NULL,
    skill_name           varchar(50)  NOT NULL,
    skill_desc           varchar(500),
    specialization_hint  varchar(200),
    CONSTRAINT pk_skill PRIMARY KEY (skill_id),
    CONSTRAINT fk_skill_attribute FOREIGN KEY (attribute_id) REFERENCES attribute (attribute_id),
    CONSTRAINT uq_skill_attribute_name UNIQUE (attribute_id, skill_name)
);

CREATE TABLE trait (
    trait_id    bigint         GENERATED ALWAYS AS IDENTITY,
    trait_name  varchar(60)    NOT NULL,
    trait_kind  varchar(15)    NOT NULL,
    max_rank    smallint,
    trait_desc  varchar(1000),
    CONSTRAINT pk_trait PRIMARY KEY (trait_id),
    CONSTRAINT uq_trait_kind_name UNIQUE (trait_kind, trait_name),
    CONSTRAINT ck_trait_kind CHECK (trait_kind IN ('ADVANTAGE', 'DISADVANTAGE', 'SPECIAL_ABILITY')),
    CONSTRAINT ck_trait_max_rank CHECK (max_rank IS NULL OR max_rank >= 1)
);

-- Association APPLIES_TO (n-n) : table de liaison
CREATE TABLE trait_genre (
    trait_id    bigint       NOT NULL,
    genre_code  varchar(10)  NOT NULL,
    CONSTRAINT pk_trait_genre PRIMARY KEY (trait_id, genre_code),
    CONSTRAINT fk_trait_genre_trait FOREIGN KEY (trait_id) REFERENCES trait (trait_id) ON DELETE CASCADE,
    CONSTRAINT fk_trait_genre_genre FOREIGN KEY (genre_code) REFERENCES genre (genre_code)
);
-- La clé primaire indexe (trait_id, genre_code) : la recherche par genre seul a besoin de son index.
CREATE INDEX ix_trait_genre_genre ON trait_genre (genre_code);

-- Les trois univers sont structurels : ils entrent avec le schéma.
INSERT INTO genre (genre_code, genre_name) VALUES
    ('FANTASY',   'D6 Fantasy'),
    ('ADVENTURE', 'D6 Aventures'),
    ('SPACE',     'D6 Galaxies');