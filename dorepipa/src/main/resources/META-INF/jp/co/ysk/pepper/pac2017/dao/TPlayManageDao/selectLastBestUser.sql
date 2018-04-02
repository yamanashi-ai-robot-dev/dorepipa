select
	us.name
from
	t_play_manage pm
	inner join t_user us
	on pm.best_user = us.user_id
where
	pm.cancel IS FALSE
and
	pm.type = 'みんなで'
order by
	pm.play_id desc
limit
	1
;
