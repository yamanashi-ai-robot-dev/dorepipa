select
	play_id,
    type,
    start_datetime,
    end_datetime,
    cancel,
    best_user
from
	t_play_manage
where
	play_id = /* playId */0
;