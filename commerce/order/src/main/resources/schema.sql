CREATE TABLE IF NOT EXISTS orders (
    order_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shopping_cart_id UUID,
    payment_id UUID,
    delivery_id UUID,
    state VARCHAR(50) NOT NULL,
    delivery_weight NUMERIC(15, 3),
    delivery_volume NUMERIC(15, 3),
    fragile BOOLEAN NOT NULL DEFAULT FALSE,
    total_price NUMERIC(19, 2) NOT NULL,
    delivery_price NUMERIC(19, 2) NOT NULL,
    product_price NUMERIC(19, 2) NOT NULL
);