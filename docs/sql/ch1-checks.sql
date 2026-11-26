\set ON_ERROR_STOP 1
-- 1. Données de test minimales
INSERT INTO attribute (genre_code, attribute_name, display_order)
VALUES ('FANTASY', 'Agilité', 1), ('FANTASY', 'Vigueur', 3);
INSERT INTO skill (attribute_id, skill_name)
SELECT attribute_id, 'Armes blanches' FROM attribute WHERE attribute_name = 'Agilité';
INSERT INTO trait (trait_name, trait_kind) VALUES ('Contact', 'ADVANTAGE');
INSERT INTO game_character (genre_code, character_name, creation_mode, is_template)
VALUES ('FANTASY', 'Barde', 'FREE', true);
INSERT INTO game_character (genre_code, character_name, creation_mode, template_id)
SELECT 'FANTASY', 'Lyra', 'TEMPLATE', character_id FROM game_character WHERE character_name = 'Barde';
INSERT INTO character_attribute
SELECT c.character_id, a.attribute_id, 11 FROM game_character c, attribute a WHERE c.character_name = 'Lyra';
INSERT INTO specialization (character_id, skill_id, specialization_name, spec_bonus_pips)
SELECT c.character_id, s.skill_id, 'Épée', 1
FROM game_character c, skill s WHERE c.character_name = 'Lyra';

-- 2. Lecture d'une fiche : attendu 2 lignes à 11 pips (3D+2)
SELECT c.character_name, a.attribute_name, ca.rating_pips
FROM game_character c
JOIN character_attribute ca USING (character_id)
JOIN attribute a USING (attribute_id)
ORDER BY a.display_order;

-- 3. Chacune de ces requêtes DOIT échouer
\set ON_ERROR_STOP 0
INSERT INTO specialization (character_id, skill_id, specialization_name, spec_bonus_pips)
SELECT c.character_id, s.skill_id, 'épée', 1
FROM game_character c, skill s WHERE c.character_name = 'Lyra';       -- uq_specialization_name
INSERT INTO trait (trait_name, trait_kind) VALUES ('Y', 'BONUS');       -- ck_trait_kind
UPDATE game_character SET template_id = character_id
WHERE character_name = 'Lyra';                                          -- ck_game_character_not_own_template
DELETE FROM skill;                                                      -- fk_specialization_skill

-- 4. Cascade : supprimer Lyra efface sa fiche ; attendu 0 et 0
DELETE FROM game_character WHERE character_name = 'Lyra';
SELECT (SELECT count(*) FROM character_attribute) AS attributs,
       (SELECT count(*) FROM specialization)      AS specialisations;
