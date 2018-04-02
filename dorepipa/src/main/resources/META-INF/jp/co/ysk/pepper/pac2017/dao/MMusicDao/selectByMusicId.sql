select
	music_id,
	note_id,
	key_id,
	start_millis,
	end_millis,
	ck_length,
	ck_faster,
	ck_slower
from
	m_music
where
	music_id = /* musicId */0
order by
	note_id
;