-- V2 : le personnage et ses valeurs propres.

CREATE TABLE game_character (
    character_id      uuid          NOT NULL DEFAULT uuidv7(),
    genre_code        varchar(10)   NOT NULL,
    template_id       uuid,
    character_name    varchar(80)   NOT NULL,
    is_template       boolean       NOT NULL DEFAULT false,
    creation_mode     varchar(10)   NOT NULL,
    is_experienced    boolean       NOT NULL DEFAULT false,
    is_human          boolean       NOT NULL DEFAULT true,
    character_points  smallint      NOT NULL DEFAULT 5,
    fate_points       smallint      NOT NULL DEFAULT 1,
    funds_pips        smallint      NOT NULL DEFAULT 9,
    move              smallint      NOT NULL DEFAULT 10,
    max_body_points   smallint,
    background        text,
    appearance        varchar(500),
    created_at        timestamptz   NOT NULL DEFAULT now(),
    updated_at        timestamptz   NOT NULL DEFAULT now(),
    version           integer       NOT NULL DEFAULT 0,
    CONSTRAINT pk_game_character PRIMARY KEY (character_id),
    CONSTRAINT fk_game_character_genre FOREIGN KEY (genre_code) REFERENCES genre (genre_code),
    CONSTRAINT fk_game_character_template FOREIGN KEY (template_id)
        REFERENCES game_character (character_id) ON DELETE SET NULL,
    CONSTRAINT ck_game_character_creation_mode CHECK (creation_mode IN ('FREE', 'TEMPLATE', 'POINTS')),
    CONSTRAINT ck_game_character_points CHECK (character_points >= 0 AND fate_points >= 0),
    CONSTRAINT ck_game_character_funds CHECK (funds_pips >= 3),
    CONSTRAINT ck_game_character_move CHECK (move >= 0),
    CONSTRAINT ck_game_character_not_own_template CHECK (template_id <> character_id)
);
CREATE INDEX ix_game_character_genre ON game_character (genre_code);
CREATE INDEX ix_game_character_template ON game_character (template_id);

-- Association HAS_ATTRIBUTE, porteuse de rating_pips
CREATE TABLE character_attribute (
    character_id  uuid      NOT NULL,
    attribute_id  bigint    NOT NULL,
    rating_pips   smallint  NOT NULL,
    CONSTRAINT pk_character_attribute PRIMARY KEY (character_id, attribute_id),
    CONSTRAINT fk_character_attribute_character FOREIGN KEY (character_id)
        REFERENCES game_character (character_id) ON DELETE CASCADE,
    CONSTRAINT fk_character_attribute_attribute FOREIGN KEY (attribute_id) REFERENCES attribute (attribute_id),
    CONSTRAINT ck_character_attribute_rating CHECK (rating_pips >= 0)
);
CREATE INDEX ix_character_attribute_attribute ON character_attribute (attribute_id);

-- Association TRAINS, porteuse de bonus_pips
CREATE TABLE character_skill (
    character_id  uuid      NOT NULL,
    skill_id      bigint    NOT NULL,
    bonus_pips    smallint  NOT NULL,
    CONSTRAINT pk_character_skill PRIMARY KEY (character_id, skill_id),
    CONSTRAINT fk_character_skill_character FOREIGN KEY (character_id)
        REFERENCES game_character (character_id) ON DELETE CASCADE,
    CONSTRAINT fk_character_skill_skill FOREIGN KEY (skill_id) REFERENCES skill (skill_id),
    CONSTRAINT ck_character_skill_bonus CHECK (bonus_pips >= 1)
);
CREATE INDEX ix_character_skill_skill ON character_skill (skill_id);

-- Association TAKES, porteuse de rank et detail
CREATE TABLE character_trait (
    character_id  uuid          NOT NULL,
    trait_id      bigint        NOT NULL,
    rank          smallint      NOT NULL,
    detail        varchar(200),
    CONSTRAINT pk_character_trait PRIMARY KEY (character_id, trait_id),
    CONSTRAINT fk_character_trait_character FOREIGN KEY (character_id)
        REFERENCES game_character (character_id) ON DELETE CASCADE,
    CONSTRAINT fk_character_trait_trait FOREIGN KEY (trait_id) REFERENCES trait (trait_id),
    CONSTRAINT ck_character_trait_rank CHECK (rank >= 1)
);
CREATE INDEX ix_character_trait_trait ON character_trait (trait_id);

-- Entité SPECIALIZATION (identifiant propre, deux dépendances 1,1)
CREATE TABLE specialization (
    specialization_id    bigint       GENERATED ALWAYS AS IDENTITY,
    character_id         uuid         NOT NULL,
    skill_id             bigint       NOT NULL,
    specialization_name  varchar(60)  NOT NULL,
    spec_bonus_pips      smallint     NOT NULL,
    CONSTRAINT pk_specialization PRIMARY KEY (specialization_id),
    CONSTRAINT fk_specialization_character FOREIGN KEY (character_id)
        REFERENCES game_character (character_id) ON DELETE CASCADE,
    CONSTRAINT fk_specialization_skill FOREIGN KEY (skill_id) REFERENCES skill (skill_id),
    CONSTRAINT ck_specialization_bonus CHECK (spec_bonus_pips >= 1)
);
-- Unicité insensible à la casse : « Épée » et « épée » sont la même Spécialisation.
CREATE UNIQUE INDEX uq_specialization_name
    ON specialization (character_id, skill_id, lower(specialization_name));
CREATE INDEX ix_specialization_skill ON specialization (skill_id);