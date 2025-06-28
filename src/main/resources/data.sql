MERGE 
 INTO mpa_rating r
USING (SELECT 1 id, 'G' rating, 'General Audiences' description UNION ALL
       SELECT 2 id, 'PG' rating, 'Parental Guidance Suggested' description UNION ALL
       SELECT 3 id, 'PG-13' rating, 'Parents Strongly Cautioned' description UNION ALL
       SELECT 4 id, 'R' rating, 'Restricted' description UNION ALL
       SELECT 5 id, 'NC-17' rating, 'Adults Only' description) t
   ON r.mpa_rating_id = t.id
WHEN MATCHED THEN
  UPDATE
     SET rating = t.rating,
         description = t.description
WHEN NOT MATCHED THEN
  INSERT (mpa_rating_id, rating, description) 
  VALUES (t.id, t.rating, t.description);

MERGE
 INTO Genre g
USING (SELECT 1 id, 'Комедия' name UNION ALL
       SELECT 2 id, 'Драма' name UNION ALL
       SELECT 3 id, 'Мультфильм' name UNION ALL
       SELECT 4 id, 'Триллер' name UNION ALL
       SELECT 5 id, 'Документальный' name UNION ALL
       SELECT 6 id, 'Боевик' name) t
   ON g.genre_id = t.id
WHEN MATCHED THEN
  UPDATE
     SET name = t.name
WHEN NOT MATCHED THEN
  INSERT (genre_id, name)
  VALUES (t.id, t.name);

COMMIT;