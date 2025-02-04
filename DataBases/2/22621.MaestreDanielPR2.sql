--EXERCISE 1

--CODE

SET search_path TO ubd_20241;

CREATE OR REPLACE FUNCTION update_report_wine(p_wine_id INT)
RETURNS REPORT_WINE_TYPE AS $$
DECLARE
    result REPORT_WINE_TYPE;
    most_frequent_customer RECORD;
BEGIN
    -- Validate if the wine exists
    IF NOT EXISTS (SELECT 1 FROM WINE WHERE wine_id = p_wine_id) THEN
        RAISE EXCEPTION 'There is no wine with the identifier provided: %', p_wine_id
            USING HINT = 'Verify that the wine ID is correct.';
    END IF;

    -- Retrieving basic wine information
    SELECT wine_id, wine_name, alcohol_content, category, price, prizes
    INTO 
        result.t_wine_id, result.t_wine_name, result.t_alcohol_content, result.t_category, result.t_price, result.t_prizes
    FROM WINE
    WHERE wine_id = p_wine_id;

--Check if the wine has been ordered
SELECT  COALESCE(SUM(quantity), 0) AS total_sold, COALESCE(COUNT(order_id), 0) AS orders
INTO result.t_total_sold, result.t_orders
FROM ORDER_LINE
WHERE wine_id = p_wine_id;

-- If there are no orders, inform the user and return the result.
IF result.t_total_sold = 0 AND result.t_orders = 0 THEN
    RAISE INFO 'Wine with ID % has never been requested in an order.', p_wine_id;
    RETURN result;
END IF;

    -- Determine the customer who has requested the wine the most.
    SELECT o.customer_id, c.customer_name, SUM(ol.quantity) AS total_quantity
    INTO most_frequent_customer
    FROM ORDER_LINE ol
    JOIN CUSTOMER_ORDER o ON ol.order_id = o.order_id
    JOIN CUSTOMER c ON o.customer_id = c.customer_id
    WHERE ol.wine_id = p_wine_id
    GROUP BY o.customer_id, c.customer_name
    ORDER BY total_quantity DESC, c.customer_name ASC
    LIMIT 1;

    result.t_customer_id := most_frequent_customer.customer_id;
    result.t_customer_name := most_frequent_customer.customer_name;

    --  Check if the record already exists in REPORT_WINE
    IF EXISTS (SELECT 1 FROM REPORT_WINE WHERE wine_id = result.t_wine_id) THEN
        -- Update existing regist
        UPDATE REPORT_WINE
        SET 
            wine_name = result.t_wine_name,
            alcohol_content = result.t_alcohol_content,
            category = result.t_category,
            price = result.t_price,
            prizes = result.t_prizes,
            total_sold = result.t_total_sold,
            orders = result.t_orders,
            customer_id = result.t_customer_id,
            customer_name = result.t_customer_name
        WHERE 
            wine_id = result.t_wine_id;
        RAISE INFO 'Record updated in REPORT_WINE for wine with ID %.', result.t_wine_id;
    ELSE
        -- Insert a new register
        INSERT INTO REPORT_WINE (
            wine_id, wine_name, alcohol_content, category, price, prizes, 
            total_sold, orders, customer_id, customer_name
        )
        VALUES (
            result.t_wine_id, result.t_wine_name, result.t_alcohol_content, 
            result.t_category, result.t_price, result.t_prizes, 
            result.t_total_sold, result.t_orders, 
            result.t_customer_id, result.t_customer_name
        );
        RAISE INFO 'New record inserted in REPORT_WINE for wine with ID %.', result.t_wine_id;
    END IF;

    RETURN result;
END;
$$ LANGUAGE plpgsql;


--TEST


--EXISTING WINE WITH ORDERS

--

SET search_path TO ubd_20241;

SELECT * FROM update_report_wine(1);


SET search_path TO ubd_20241;

SELECT * FROM REPORT_WINE;


--EXISTING WINE WITHOUT ORDER

--Checking of wines that have not been ordered, see if they have an id and name.

SET search_path TO ubd_20241;

SELECT w.wine_id, w.wine_name
FROM WINE w
LEFT JOIN ORDER_LINE ol ON w.wine_id = ol.wine_id
WHERE ol.quantity IS NULL;

--Comprobar vino sin ventas con wine_id=26.
SET search_path TO ubd_20241;

select * from update_report_wine(26)

--NON-EXISTENT WINE

--Comprobar vino que no tiene wine_id, en este caso el 123.

SET search_path TO ubd_20241;

select * from update_report_wine(123);

--UPDATING THE TABLE IF DATA CHANGES

-- Update the total amount of wines sold (total_sold) for a specific customer 
UPDATE REPORT_WINE SET total_sold = total_sold + 5 -- Increase by 5 (modify as necessary) 
WHERE customer_id = 12 -- Specify corresponding customer_id 
AND wine_id = 1; -- Specify the matching wine_id



--DELETE CUSTOMER TO SEE IF THE TABLE IS UPDATED

DELETE FROM ORDER_LINE
WHERE order_id IN (
    SELECT order_id
    FROM CUSTOMER_ORDER
    WHERE customer_id = 12
);

DELETE FROM CUSTOMER_ORDER
WHERE customer_id = 12;

DELETE FROM CUSTOMER
WHERE customer_id = 12;




--EXERCISE 2

--CODE

SET search_path TO ubd_20241;

-- Create the trigger that keeps the stock updated in WINE
CREATE OR REPLACE FUNCTION update_stock()
RETURNS TRIGGER AS $$
BEGIN
    -- Handling for a new insertion in ORDER_LINE
    IF TG_OP = 'INSERT' THEN
        -- Check if there is sufficient stock before placing an order
        IF (SELECT stock FROM WINE WHERE wine_id = NEW.wine_id) < NEW.quantity THEN
            RAISE EXCEPTION 'Insufficient stock to complete the order for wine with ID: %.', NEW.wine_id;
        END IF;
        
        -- Subtract the quantity of the stock in WINE
        UPDATE WINE
        SET stock = stock - NEW.quantity
        WHERE wine_id = NEW.wine_id;
    
    -- Handling for an update in ORDER_LINE
    ELSIF TG_OP = 'UPDATE' THEN
        -- Calculate the difference between the new quantity and the previous quantity.
        DECLARE
            quantity_difference INTEGER := NEW.quantity - OLD.quantity;
        BEGIN
            --  If the quantity has increased, check if there is enough stock
            IF quantity_difference > 0 THEN
                IF (SELECT stock FROM WINE WHERE wine_id = NEW.wine_id) < quantity_difference THEN
                    RAISE EXCEPTION 'Insufficient stock to increase the order quantity of the wine with ID: %', NEW.wine_id;
                END IF;
                -- If sufficient stock is available, subtract the difference
                UPDATE WINE
                SET stock = stock - quantity_difference
                WHERE wine_id = NEW.wine_id;
            ELSE
                -- If the quantity has decreased, increase the stock in WINE
                UPDATE WINE
                SET stock = stock + ABS(quantity_difference)
                WHERE wine_id = NEW.wine_id;
            END IF;
        END;
    
    -- Handling for elimination in ORDER_LINE
    ELSIF TG_OP = 'DELETE' THEN
        -- Return the removed quantity to stock in WIN
        UPDATE WINE
        SET stock = stock + OLD.quantity
        WHERE wine_id = OLD.wine_id;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Assign trigger to ORDER_LINE table for INSERT, UPDATE and DELETE operations
CREATE TRIGGER trigger_update_stock
AFTER INSERT OR UPDATE OR DELETE ON ORDER_LINE
FOR EACH ROW
EXECUTE FUNCTION update_stock();



--TEST
--FIRST STEP
-- Insert a new order in CUSTOMER_ORDER so that `order_id = 100`.
INSERT INTO CUSTOMER_ORDER (order_id, customer_id, order_date, order_status, order_amount)
VALUES (100, 1, '2024-11-15', 'pending', 150.00);

-- Add an order line for ‘Vega Sicilia Único’ (wine_id = 3) with quantity = 5
INSERT INTO ORDER_LINE (order_id, order_line_id, wine_id, quantity, discount) 
VALUES (100, 1, 3, 5, NULL);

-- Check stock after insertion 
SELECT stock FROM WINE WHERE wine_id = 3;


--SECOND STEP
-- Insert a new order in CUSTOMER_ORDER with order_id = 101
INSERT INTO CUSTOMER_ORDER (order_id, customer_id, order_date, order_status, order_amount)
VALUES (101, 1, '2024-11-15', 'pending', 450.00);


-- Try adding an order line for "Vega Sicilia Único" (wine_id = 3) with quantity = 30
INSERT INTO ORDER_LINE (order_id, order_line_id, wine_id, quantity, discount) 
VALUES (101, 1, 3, 30, NULL);


--THIRT STEP

-- Increase the quantity in the previous order line (order 100) from 5 to 8
UPDATE ORDER_LINE
SET quantity = 8
WHERE order_id = 100 AND order_line_id = 1;

-- Check stock after insertion
SELECT stock FROM WINE WHERE wine_id = 3;


--FOURTH STEP

-- Try to increase the quantity in order 100 from 8 to 30 (not enough stock).
UPDATE ORDER_LINE
SET quantity = 30
WHERE order_id = 100 AND order_line_id = 1;


--FIFTH STEP

-- Reduce order quantity with order_id =100 from 8 to 3
UPDATE ORDER_LINE
SET quantity = 3
WHERE order_id = 100 AND order_line_id = 1;

-- Check the stock after the update
SELECT stock FROM WINE WHERE wine_id = 3;


--SIXTH STEP
-- Delete the order line (order_id= 100) for ‘Vega Sicilia Único’
DELETE FROM ORDER_LINE
WHERE order_id = 100 AND order_line_id = 1;


-- Check stock after elimination 
SELECT stock FROM WINE WHERE wine_id = 3;


















