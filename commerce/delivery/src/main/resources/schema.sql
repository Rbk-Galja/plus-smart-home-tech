CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_country VARCHAR(100),
    from_city    VARCHAR(100),
    from_street  VARCHAR(150),
    from_house   VARCHAR(50),
    from_flat    VARCHAR(50),
    to_country   VARCHAR(100),
    to_city      VARCHAR(100),
    to_street    VARCHAR(150),
    to_house     VARCHAR(50),
    to_flat      VARCHAR(50),
    order_id UUID NOT NULL,
    delivery_state VARCHAR(50) NOT NULL
);