INSERT INTO review (album_id, user_id, rating, comment)
SELECT a.id, u.id, v.rating, v.comment
FROM (VALUES
          ('OK Computer',            'alice', 5, 'A landmark record, still sounds ahead of its time.'),
          ('OK Computer',            'bob',   5, 'Paranoid Android alone is worth it.'),
          ('OK Computer',            'carol', 4, 'Moody and brilliant.'),
          ('Kind of Blue',           'alice', 5, 'The best entry point into jazz.'),
          ('Kind of Blue',           'dave',  5, 'Timeless.'),
          ('Abbey Road',             'bob',   5, 'The side two medley is perfect.'),
          ('Abbey Road',             'carol', 4, 'Polished and endlessly listenable.'),
          ('To Pimp a Butterfly',    'dave',  5, 'Ambitious and dense, rewards repeat listens.'),
          ('To Pimp a Butterfly',    'alice', 4, 'Powerful, though not an easy listen.'),
          ('Rumours',                'carol', 5, 'Every track is a hit.'),
          ('Rumours',                'bob',   4, 'Drama that turned into great songs.'),
          ('Random Access Memories', 'dave',  4, 'Warm, human-sounding electronic music.'),
          ('Random Access Memories', 'alice', 3, 'Great singles, a bit uneven overall.'),
          ('Blonde',                 'carol', 5, 'Intimate and strange in the best way.'),
          ('Blonde',                 'bob',   4, 'Grows on you.'),
          ('Currents',               'dave',  4, 'Dreamy production throughout.')
     ) AS v(title, username, rating, comment)
         JOIN album a ON a.title = v.title
         JOIN app_user u ON u.username = v.username;