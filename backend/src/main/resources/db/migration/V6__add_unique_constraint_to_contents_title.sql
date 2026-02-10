DELETE FROM contents a USING contents b WHERE a.id < b.id AND a.title = b.title;
ALTER TABLE contents ADD CONSTRAINT uq_contents_title UNIQUE (title);
