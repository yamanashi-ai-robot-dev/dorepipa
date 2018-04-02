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
	mp.play_id = /* currPlayId */0
	and
	mp.user_id = /* userId */0
	and
	not exists (
		select
			mp2.mistake_point_id
		from
			t_mistake_points mp2
		where
			mp2.play_id = /* lastPlayId */0
			and
			mp2.user_id = /* userId */0
			and
			mp2.note_id = mp.note_id
			and
			mp2.mistake_cd = mp.mistake_cd
	)
order by
	mp.deducted_point desc
limit
	1