INSERT INTO genero_musical (nome, slug) VALUES 
('Rock Alternativo', 'rock-alternativo'),
('Hip Hop', 'hip-hop'),
('Sertanejo', 'sertanejo'),
('Hard Rock', 'hard-rock')
ON CONFLICT (slug) DO NOTHING;

INSERT INTO artista (nome, pais_origem, ano_formacao) VALUES 
('Serj Tankian', 'USA', 1994),
('Mike Shinoda', 'USA', 1996),
('Michel Teló', 'BRA', 1997),
('Guns N'' Roses', 'USA', 1985); 

INSERT INTO album (titulo, ano_lancamento) VALUES 
('Harakiri', 2012),
('Black Blooms', 2012),
('The Rough Dog', 2007),
('The Rising Tied', 2005),
('Post Traumatic', 2018),
('Post Traumatic EP', 2018),
('Where''d You Go', 2006),
('Bem Sertanejo', 2014),
('Bem Sertanejo - O Show (Ao Vivo)', 2017),
('Bem Sertanejo - (1ª Temporada) - EP', 2014),
('Use Your Illusion I', 1991),
('Use Your Illusion II', 1991),
('Greatest Hits', 2004);

INSERT INTO imagem_album (album_id, url, nome_arquivo)
SELECT id, 'serj-harakiri.jpg', 'Capa Frontal' FROM album WHERE titulo = 'Harakiri';

INSERT INTO imagem_album (album_id, url, nome_arquivo)
SELECT id, 'serj-black-blooms.jpg', 'Capa Frontal' FROM album WHERE titulo = 'Black Blooms';

INSERT INTO imagem_album (album_id, url, nome_arquivo)
SELECT id, 'serj-rough-dog.jpg', 'Capa Frontal' FROM album WHERE titulo = 'The Rough Dog';

INSERT INTO imagem_album (album_id, url, nome_arquivo)
SELECT id, 'mike-rising-tied.jpg', 'Capa Frontal' FROM album WHERE titulo = 'The Rising Tied';

INSERT INTO imagem_album (album_id, url, nome_arquivo)
SELECT id, 'mike-post-traumatic.jpg', 'Capa Frontal' FROM album WHERE titulo = 'Post Traumatic';

INSERT INTO imagem_album (album_id, url, nome_arquivo)
SELECT id, 'mike-post-traumatic-ep.jpg', 'Capa Frontal' FROM album WHERE titulo = 'Post Traumatic EP';

INSERT INTO imagem_album (album_id, url, nome_arquivo)
SELECT id, 'mike-whered-you-go.jpg', 'Capa Frontal' FROM album WHERE titulo = 'Where''d You Go';

INSERT INTO imagem_album (album_id, url, nome_arquivo)
SELECT id, 'telo-bem-sertanejo.jpg', 'Capa Frontal' FROM album WHERE titulo = 'Bem Sertanejo';

INSERT INTO imagem_album (album_id, url, nome_arquivo)
SELECT id, 'telo-bem-sertanejo-back.jpg', 'Contracapa' FROM album WHERE titulo = 'Bem Sertanejo';

INSERT INTO imagem_album (album_id, url, nome_arquivo)
SELECT id, 'telo-bem-sertanejo-live.jpg', 'Capa Frontal' FROM album WHERE titulo = 'Bem Sertanejo - O Show (Ao Vivo)';

INSERT INTO imagem_album (album_id, url, nome_arquivo)
SELECT id, 'telo-bem-sertanejo-ep.jpg', 'Capa Frontal' FROM album WHERE titulo = 'Bem Sertanejo - (1ª Temporada) - EP';

INSERT INTO imagem_album (album_id, url, nome_arquivo)
SELECT id, 'gnr-illusion-1.jpg', 'Capa Frontal' FROM album WHERE titulo = 'Use Your Illusion I';

INSERT INTO imagem_album (album_id, url, nome_arquivo)
SELECT id, 'gnr-illusion-2.jpg', 'Capa Frontal' FROM album WHERE titulo = 'Use Your Illusion II';

INSERT INTO imagem_album (album_id, url, nome_arquivo)
SELECT id, 'gnr-greatest-hits.jpg', 'Capa Frontal' FROM album WHERE titulo = 'Greatest Hits';

INSERT INTO album_artista (album_id, artista_id)
SELECT al.id, ar.id FROM album al, artista ar 
WHERE ar.nome = 'Serj Tankian' AND al.titulo IN ('Harakiri', 'Black Blooms', 'The Rough Dog');

INSERT INTO album_artista (album_id, artista_id)
SELECT al.id, ar.id FROM album al, artista ar 
WHERE ar.nome = 'Mike Shinoda' AND al.titulo IN ('The Rising Tied', 'Post Traumatic', 'Post Traumatic EP', 'Where''d You Go');

INSERT INTO album_artista (album_id, artista_id)
SELECT al.id, ar.id FROM album al, artista ar 
WHERE ar.nome = 'Michel Teló' AND al.titulo LIKE 'Bem Sertanejo%';

INSERT INTO album_artista (album_id, artista_id)
SELECT al.id, ar.id FROM album al, artista ar 
WHERE ar.nome = 'Guns N'' Roses' AND al.titulo IN ('Use Your Illusion I', 'Use Your Illusion II', 'Greatest Hits');

INSERT INTO album_genero (album_id, genero_id)
SELECT al.id, gm.id FROM album al, genero_musical gm
WHERE gm.slug = 'rock-alternativo' AND al.titulo IN ('Harakiri', 'Black Blooms', 'The Rough Dog');

INSERT INTO album_genero (album_id, genero_id)
SELECT al.id, gm.id FROM album al, genero_musical gm
WHERE gm.slug = 'hip-hop' AND al.titulo IN ('The Rising Tied', 'Post Traumatic', 'Post Traumatic EP', 'Where''d You Go');

INSERT INTO album_genero (album_id, genero_id)
SELECT al.id, gm.id FROM album al, genero_musical gm
WHERE gm.slug = 'sertanejo' AND al.titulo LIKE 'Bem Sertanejo%';

INSERT INTO album_genero (album_id, genero_id)
SELECT al.id, gm.id FROM album al, genero_musical gm
WHERE gm.slug = 'hard-rock' AND al.titulo IN ('Use Your Illusion I', 'Use Your Illusion II', 'Greatest Hits');