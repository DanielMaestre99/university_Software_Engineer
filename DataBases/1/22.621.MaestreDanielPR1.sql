--All the SQL statements you have executed in the completion of the practice.

--EXERCISE 1/1
CREATE TABLE WINERY (
    winery_id SERIAL PRIMARY KEY,
    winery_name VARCHAR(100) NOT NULL,
    town VARCHAR(100) NOT NULL,
    established_year INT,
    winery_phone VARCHAR(20),
    sales_representative VARCHAR(100) NOT NULL
);


--EXERCISE 1/2.1
--a)
--alter add colum price of win
ALTER TABLE WINE
ADD COLUMN prizes INT;

--b)
-- Remove existing restriction chk_alcohol_content
ALTER TABLE WINE
DROP CONSTRAINT IF EXISTS chk_alcohol_content;

-- Defining the alcohol content by type of wine
ALTER TABLE WINE
ADD CONSTRAINT chk_alcohol_content CHECK (
    (color = 'white' AND alcohol_content BETWEEN 10 AND 11) OR
    (color = 'rosé' AND alcohol_content BETWEEN 11 AND 15) OR
    (color = 'red' AND alcohol_content BETWEEN 13 AND 18)
);
--c)
-- Define winery_id in WINE as foreign key to WINERY
ALTER TABLE WINE
ADD CONSTRAINT fk_winery FOREIGN KEY (winery_id) REFERENCES WINERY(winery_id);

--EXERCISE 1/2.2
--a)
--Add column for total order amount  
ALTER TABLE CUSTOMER_ORDER
ADD COLUMN order_amount DECIMAL(10, 2) CHECK (order_amount >= 0);
--b)
-- Add order reference column with specific formatting XXX-NN-XXX//X-Capital leters N-> Numbers 
ALTER TABLE CUSTOMER_ORDER
ADD COLUMN order_reference VARCHAR(12) CHECK (order_reference SIMILAR TO '[A-Z]{3}-[0-9]{2}-[A-Z]{4}');


--EXERCISE 1/2.3
-- Add discount column with a maximum limit of 99.99%.
ALTER TABLE ORDER_LINE
ADD COLUMN discount DECIMAL(5, 2) CHECK (discount >= 0 AND discount <= 99.99);

--EXERCISE 1/2.4
--a)
-- Avoid duplicate capital names
ALTER TABLE ZONE
ADD CONSTRAINT unique_capital_town UNIQUE (capital_town);

--b)
-- Eliminate the surface column
ALTER TABLE ZONE
DROP COLUMN surface;


--EXERCISE 2/1

SET search_path TO ubd_20241;

SELECT CUSTOMER.customer_id, customer_name, COUNT(CUSTOMER_ORDER.order_id) AS total_orders, MAX(order_date) AS most_recent_order
FROM CUSTOMER_ORDER
JOIN CUSTOMER ON CUSTOMER_ORDER.customer_id = CUSTOMER.customer_id
WHERE customer_status = 'active'
GROUP BY CUSTOMER.customer_id, customer_name
ORDER BY total_orders DESC, customer_name ASC
LIMIT 5;

--EXERCISE 2/2

SET search_path TO ubd_20241;

SELECT WINE.wine_name, WINE.category, WINE.color, ZONE.zone_name, 
    COUNT(WINE.wine_id) OVER (PARTITION BY WINE.zone_id) AS total_wine_in_zone
FROM WINE
JOIN ZONE ON WINE.zone_id = ZONE.zone_id
WHERE WINE.zone_id IN (
        SELECT zone_id
        FROM WINE
        GROUP BY zone_id
        HAVING COUNT(wine_id) >= 5
    )
ORDER BY 
    total_wine_in_zone DESC, ZONE.zone_name, WINE.wine_name;

--EXERCISE 2/3

SET search_path TO ubd_20241;


CREATE VIEW gr_not_mediterranean AS
SELECT WINE.wine_name, WINE.category, GRAPE_VARIETY.grape_name, ZONE.zone_name
FROM WINE
JOIN WINE_GRAPE ON WINE.wine_id = WINE_GRAPE.wine_id
JOIN GRAPE_VARIETY ON WINE_GRAPE.grape_id = GRAPE_VARIETY.grape_id
JOIN ZONE ON WINE.zone_id = ZONE.zone_id
WHERE WINE.category = 'grand reserve' AND ZONE.climate != 'Mediterranean'
ORDER BY WINE.wine_name, GRAPE_VARIETY.grape_name;

--Programm to view the result
SET search_path TO ubd_20241;

SELECT * FROM gr_not_mediterranean;

--EXERCISE 3

SET search_path TO ubd_20241;

UPDATE ORDER_LINE
SET discount = 10
WHERE order_id IN (
    SELECT order_id FROM CUSTOMER_ORDER
    WHERE order_status = 'pending'
    AND order_id NOT IN (
        SELECT ORDER_LINE.order_id
        FROM ORDER_LINE 
        JOIN WINE ON ORDER_LINE.wine_id = WINE.wine_id
        JOIN ZONE ON WINE.zone_id = ZONE.zone_id
        WHERE ZONE.zone_name = 'Ribera del Duero'
    )
    AND discount IS NULL
);

--Programm check

SET search_path TO ubd_20241;

SELECT ORDER_LINE.order_id, ORDER_LINE.order_line_id, ORDER_LINE.wine_id, ORDER_LINE.discount,CUSTOMER_ORDER.order_status,
		ZONE.zone_name AS dop_zone_name
FROM ORDER_LINE
JOIN CUSTOMER_ORDER ON ORDER_LINE.order_id = CUSTOMER_ORDER.order_id
JOIN WINE ON ORDER_LINE.wine_id = WINE.wine_id
JOIN ZONE ON WINE.zone_id = ZONE.zone_id
WHERE ORDER_LINE.discount = 10 AND CUSTOMER_ORDER.order_status = 'pending'AND ZONE.zone_name != 'Ribera del Duero'
ORDER BY ORDER_LINE.order_id, ORDER_LINE.order_line_id;




