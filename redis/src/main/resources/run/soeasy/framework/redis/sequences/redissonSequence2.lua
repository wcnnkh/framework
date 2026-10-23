local key = KEYS[1]
if redis.call('exists', key) == 1 then
    return redis.call('incrby', key, tonumber(ARGV[1]))
else
	local default_val = tonumber(ARGV[2])
    redis.call('set', key, default_val)
    return default_val
end