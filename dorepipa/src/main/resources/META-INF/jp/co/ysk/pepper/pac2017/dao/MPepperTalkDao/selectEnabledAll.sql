select
	id,
	keyword,
	serial,
	text,
	enable
from
	m_pepper_talk
where
	enable = true
order by
	keyword,
	serial
;