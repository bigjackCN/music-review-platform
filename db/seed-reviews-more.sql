-- More demo users and reviews so the positive-review rates are meaningful.
-- Every demo user shares alice's password hash (demo12345), so no hashes are stored here.
INSERT INTO app_user (username, password, email)
SELECT n, (SELECT password FROM app_user WHERE username = 'alice'), n || '@example.com'
FROM unnest(ARRAY['erin', 'frank', 'grace', 'henry', 'ivy', 'jack', 'kira', 'leo']) AS n
ON CONFLICT DO NOTHING;

INSERT INTO review (album_id, user_id, rating, comment)
SELECT a.id, u.id, v.rating, v.comment
FROM (VALUES
  ('OK Computer',            'erin',  5, 'Still the benchmark for the genre.'),
  ('OK Computer',            'frank', 2, 'I admire it more than I enjoy it.'),
  ('OK Computer',            'grace', 4, 'Cold, but in a way that works.'),
  ('Kind of Blue',           'erin',  5, 'Put it on and everything slows down.'),
  ('Kind of Blue',           'frank', 4, 'A great place to start with jazz.'),
  ('Kind of Blue',           'grace', 5, 'Perfect late-night record.'),
  ('Abbey Road',             'erin',  4, 'Warm and carefully made.'),
  ('Abbey Road',             'henry', 5, 'The closing run is unbeatable.'),
  ('Abbey Road',             'ivy',   3, 'Brilliant second half, uneven first.'),
  ('Abbey Road',             'jack',  5, 'Sounds fresh every time.'),
  ('To Pimp a Butterfly',    'erin',  5, 'Dense, ambitious and rewarding.'),
  ('To Pimp a Butterfly',    'frank', 3, 'Important, but hard to get through in one go.'),
  ('To Pimp a Butterfly',    'kira',  4, 'Gets better with every listen.'),
  ('To Pimp a Butterfly',    'leo',   2, 'Too sprawling for me.'),
  ('Rumours',                'frank', 5, 'Not a weak song on it.'),
  ('Rumours',                'grace', 3, 'Good, though overplayed.'),
  ('Rumours',                'henry', 4, 'The harmonies carry it.'),
  ('Rumours',                'leo',   5, 'Pure pop craftsmanship.'),
  ('Random Access Memories', 'erin',  2, 'Too polished for my taste.'),
  ('Random Access Memories', 'frank', 3, 'Two great singles, filler between.'),
  ('Random Access Memories', 'ivy',   4, 'Lovely production.'),
  ('Random Access Memories', 'jack',  2, 'Expected more energy.'),
  ('Random Access Memories', 'kira',  5, 'A big, generous record.'),
  ('Blonde',                 'ivy',   5, 'Quiet and devastating.'),
  ('Blonde',                 'jack',  5, 'Nothing else sounds like it.'),
  ('Blonde',                 'leo',   4, 'Takes patience, pays off.'),
  ('Blonde',                 'grace', 3, 'Beautiful in parts, meandering in others.'),
  ('Currents',               'erin',  3, 'Pretty, a little samey.'),
  ('Currents',               'frank', 4, 'Great sound design.'),
  ('Currents',               'henry', 2, 'Style over songs.'),
  ('Currents',               'ivy',   3, 'Enjoyable, not essential.'),
  ('Currents',               'kira',  4, 'Perfect for a long drive.'),
  ('Currents',               'leo',   5, 'Warm, hazy and catchy.')
) AS v(title, username, rating, comment)
JOIN album a ON a.title = v.title
JOIN app_user u ON u.username = v.username
ON CONFLICT (user_id, album_id) DO NOTHING;
