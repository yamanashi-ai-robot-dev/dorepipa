select
    user_id,
    name,
    face_id,
    voice_id,
    one_time
from
	t_user
where
	face_id = /* faceId */''
;