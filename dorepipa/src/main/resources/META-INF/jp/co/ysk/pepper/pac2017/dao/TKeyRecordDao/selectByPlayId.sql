select
    key_record_id,
    play_id,
    device_id,
    key_id,
    status,
    datetime
from
	t_key_record
where
	play_id = /* playId */''
;