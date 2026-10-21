local key = KEYS[1]
if redis.call('exists', key) == 1 then
    return redis.call('incrby', key, tonumber(ARGV[1]))
else
    return nil
end