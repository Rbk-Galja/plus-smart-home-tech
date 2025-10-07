CREATE TABLE IF NOT EXISTS warehouse_products (
    product_id UUID PRIMARY KEY,
    fragile BOOLEAN NOT NULL,
    weight NUMERIC(10,2) NOT NULL CHECK (weight > 0),
    width NUMERIC(10,2) NOT NULL CHECK (width > 0),
    height NUMERIC(10,2) NOT NULL CHECK (height > 0),
    depth NUMERIC(10,2) NOT NULL CHECK (depth > 0),
    quantity BIGINT NOT NULL DEFAULT 0 CHECK (quantity >= 0)
);

CREATE TABLE IF NOT EXISTS warehouse_address (
    id BIGSERIAL PRIMARY KEY,
    country VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(255) NOT NULL,
    house VARCHAR(50) NOT NULL,
    flat VARCHAR(50)
);

create table if not exists bookings (
    shopping_cart_id uuid primary key,
    delivery_weight  double precision not null,
    delivery_volume  double precision not null,
    fragile boolean not null,
    order_id uuid
);

create table if not exists booking_products (
    shopping_cart_id uuid references bookings (shopping_cart_id) on delete cascade primary key,
    product_id uuid not null,
    quantity integer
);