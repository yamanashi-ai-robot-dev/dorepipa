select
    user_id
from
	t_score
where
	play_id = /* playId */''
order by
	score desc
limit
	1
;