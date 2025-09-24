CREATE TABLE  IF NOT EXISTS warehouse_products (
    product_id UUID PRIMARY KEY,
    fragile BOOLEAN NOT NULL,
    weight NUMERIC(10,2) NOT NULL CHECK (weight > 0),
    width NUMERIC(10,2) NOT NULL CHECK (width > 0),
    height NUMERIC(10,2) NOT NULL CHECK (height > 0),
    depth NUMERIC(10,2) NOT NULL CHECK (depth > 0),
    quantity BIGINT NOT NULL DEFAULT 0 CHECK (quantity >= 0)
);

CREATE TABLE  IF NOT EXISTS warehouse_address (
    id BIGSERIAL PRIMARY KEY,
    country VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(255) NOT NULL,
    house VARCHAR(50) NOT NULL,
    flat VARCHAR(50)
);