select
	sc.play_id,
    sc.user_id,
    sc.music_id,
    sc.score_of_percent,
    sc.score,
    sc.detail,
    sc.scale_miss_pt,
    sc.tempo_miss_pt,
    sc.shorter_miss_pt,
    sc.little_miss_pt,
    dul.device_id,
    us.name,
    us.one_time
from
	t_score sc
	inner join t_user us
		on sc.user_id = us.user_id
	inner join t_device_user_link dul
		on sc.play_id = dul.play_id
		and sc.user_id = dul.user_id
where
	sc.play_id = /* playId */''
order by
	sc.score desc
limit
	1
;