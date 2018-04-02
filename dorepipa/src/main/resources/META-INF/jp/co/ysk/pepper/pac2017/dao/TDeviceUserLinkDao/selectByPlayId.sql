select
    play_id,
    device_id,
    user_id
from
	t_device_user_link
where
	play_id = /* playId */''
;