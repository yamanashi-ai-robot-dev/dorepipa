insert into t_device_user_link (
    play_id,
    device_id,
    user_id
)
select
	/* newPlayId */0,
	device_id,
	user_id
from
	t_device_user_link
where
	play_id = /* oldPlayId */0
;
