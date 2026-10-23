local key = KEYS[1]
local step = tonumber(ARGV[1]) or 1
local default_val = tonumber(ARGV[2])

if redis.call('exists', key) == 1 then
    return redis.call('incrby', key, step)
else
    if default_val ~= nil then
        redis.call('set', key, default_val)
        return default_val
    else
        return nil
    end
end