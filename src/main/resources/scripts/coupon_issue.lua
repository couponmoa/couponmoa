-- KEYS[1]: stock key (ex. coupon:1:stock)
-- KEYS[2]: user set key (ex. coupon:1)
-- ARGV[1]: userId

-- 중복 발급 검사
if redis.call("SISMEMBER", KEYS[2], ARGV[1]) == 1 then
    return 2
end

-- 잔여 재고 검사
local stock = tonumber(redis.call("GET", KEYS[1]))
if not stock then
    return 1
end

if stock <= 0 then
    return 3
end

-- 쿠폰 발급 성공
redis.call("DECR", KEYS[1])
redis.call("SADD", KEYS[2], ARGV[1])
return 0