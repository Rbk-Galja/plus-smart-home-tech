CREATE TABLE IF NOT EXISTS products (
    product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    image_src VARCHAR(500),
    quantity_state VARCHAR(20) NOT NULL CHECK (quantity_state IN ('ENDED','FEW','ENOUGH','MANY')),
    product_state VARCHAR(20) NOT NULL CHECK (product_state IN ('ACTIVE','DEACTIVATE')),
    product_category VARCHAR(20) NOT NULL CHECK (product_category IN ('LIGHTING','CONTROL','SENSORS')),
    price NUMERIC(12,2) NOT NULL CHECK (price >= 1)
);

CREATE INDEX IF NOT EXISTS idx_products_category ON products(product_category);
CREATE INDEX IF NOT EXISTS idx_products_state ON products(product_state);
CREATE INDEX IF NOT EXISTS idx_products_quantity_state ON products(quantity_state);
CREATE INDEX IF NOT EXISTS idx_products_category_price ON products(product_category, price);