(
	select
	    play_id,
	    device_id,
	    key_id,
	    start_millis,
	    end_millis
	from
		t_analyzed_note
	where
		play_id = /* playId */''
		and
		device_id = /* deviceId */''
		and
		key_id <> /* keyId */''
		and
		start_millis >= /* baseStartMillis */0
		and
		start_millis < /* baseEndMillis */0
) union (
	select
	    play_id,
	    device_id,
	    key_id,
	    start_millis,
	    end_millis
	from
		t_analyzed_note
	where
		play_id = /* playId */''
		and
		device_id = /* deviceId */''
		and
		key_id <> /* keyId */''
		and
		/* baseStartMillis */0 >= start_millis
		and
		/* baseStartMillis */0 < end_millis
)
order by device_id, start_millis;