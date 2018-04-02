select
	measure,
	num_in_measure,
	key_id,
	mistake_cd
from
	(
	select
		mu.measure,
		mu.num_in_measure,
		mu.key_id,
		mp.mistake_cd,
		(mp.deducted_point + mp2.deducted_point) AS total_deduction
	from
		t_mistake_points mp
		inner join t_mistake_points mp2
			on mp.user_id = mp2.user_id
			and mp.note_id = mp2.note_id
			and mp.mistake_cd = mp2.mistake_cd
		inner join m_music mu
			on mp.music_id = mu.music_id
			and mp.note_id = mu.note_id
	where
		mp.play_id = /* currPlayId */0
		and
		mp2.play_id = /* lastPlayId */0
		and
		mp.user_id = /* userId */0
	)
order by
	total_deduction desc
limit
	1