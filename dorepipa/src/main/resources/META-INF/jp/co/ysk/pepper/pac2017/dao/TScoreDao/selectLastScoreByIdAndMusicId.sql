select
	play_id,
    user_id,
    music_id,
    score_of_percent,
    score,
    detail,
    scale_miss_pt,
    tempo_miss_pt,
    shorter_miss_pt,
    little_miss_pt
from
	t_score sc
where
	user_id = /* userId */0
	and
	music_id = /* musicId */0
	and
	play_id < /* playId */0
order by
	play_id desc
limit
	1
;