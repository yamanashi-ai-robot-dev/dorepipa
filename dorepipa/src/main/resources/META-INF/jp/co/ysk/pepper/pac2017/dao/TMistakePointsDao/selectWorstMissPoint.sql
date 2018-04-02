select
	mu.measure,
	mu.num_in_measure,
	mu.key_id,
	mp.mistake_cd
from
	t_mistake_points mp
	inner join m_music mu
		on mp.music_id = mu.music_id
		and mp.note_id = mu.note_id
where
	mp.play_id = /* playId */0
	and
	mp.user_id = /* userId */0
order by
	mp.deducted_point desc
limit
	1
