drop database nodo;
create database nodo;
use nodo;

create table category
(
    created_date  date                        null,
    modified_date date                        null,
    id            bigint auto_increment primary key,
    category_code varchar(255) UNIQUE         null,
    created_by    varchar(255)                null,
    description   varchar(255)                null,
    modified_by   varchar(255)                null,
    name          varchar(255)                null,
    image         varchar(255)                null,
    status        enum ('ACTIVE', 'INACTIVE') null
);

create table product
(
    created_date  date                                             null,
    modified_date date                                             null,
    price         double                                           null,
    id            bigint auto_increment primary key,
    quantity      bigint                                           null,
    created_by    varchar(255)                                     null,
    description   varchar(255)                                     null,
    modified_by   varchar(255)                                     null,
    name          varchar(255)                                     null,
    image         varchar(255)                                     null,
    product_code  varchar(255) UNIQUE                              null,
    status        enum ('ACTIVE', 'INACTIVE') null
);

create table product_category
(
    created_date  date         null,
    modified_date date         null,
    status        enum ('ACTIVE', 'INACTIVE') null,
    category_id   bigint       null,
    id            bigint auto_increment primary key,
    product_id    bigint       null,
    created_by    varchar(255) null,
    modified_by   varchar(255) null,
    constraint FK2k3smhbruedlcrvu6clued06x foreign key (product_id) references product (id),
    constraint FKkud35ls1d40wpjb5htpp14q4e foreign key (category_id) references category (id)
);

INSERT INTO category (created_date, modified_date, category_code, created_by, description, modified_by, name, status)
VALUES
('2024-09-01', '2024-09-01', 'C0001', 'admin', 'Electronics description', 'admin', 'Electronics', 'ACTIVE'),
('2024-09-02', '2024-09-02', 'C0002', 'admin', 'Clothing description', 'admin', 'Clothing', 'ACTIVE'),
('2024-09-03', '2024-09-03', 'C0003', 'admin', 'Home Appliances description', 'admin', 'Home Appliances', 'ACTIVE'),
('2024-09-04', '2024-09-04', 'C0004', 'admin', 'Books description', 'admin', 'Books', 'ACTIVE'),
('2024-09-05', '2024-09-05', 'C0005', 'admin', 'Toys description', 'admin', 'Toys', 'ACTIVE'),
('2024-09-06', '2024-09-06', 'C0006', 'admin', 'Beauty Products description', 'admin', 'Beauty', 'ACTIVE'),
('2024-09-07', '2024-09-07', 'C0007', 'admin', 'Sports Equipment description', 'admin', 'Sports', 'ACTIVE'),
('2024-09-08', '2024-09-08', 'C0008', 'admin', 'Automotive description', 'admin', 'Automotive', 'ACTIVE'),
('2024-09-09', '2024-09-09', 'C0009', 'admin', 'Furniture description', 'admin', 'Furniture', 'ACTIVE'),
('2024-09-10', '2024-09-10', 'C0010', 'admin', 'Office Supplies description', 'admin', 'Office Supplies', 'ACTIVE'),
('2024-09-11', '2024-09-11', 'C0011', 'admin', 'Garden Supplies description', 'admin', 'Garden Supplies', 'ACTIVE'),
('2024-09-12', '2024-09-12', 'C0012', 'admin', 'Music Instruments description', 'admin', 'Music Instruments', 'ACTIVE'),
('2024-09-13', '2024-09-13', 'C0013', 'admin', 'Health Products description', 'admin', 'Health Products', 'ACTIVE'),
('2024-09-14', '2024-09-14', 'C0014', 'admin', 'Pet Supplies description', 'admin', 'Pet Supplies', 'ACTIVE'),
('2024-09-15', '2024-09-15', 'C0015', 'admin', 'Baby Products description', 'admin', 'Baby Products', 'ACTIVE');

INSERT INTO product (created_date, modified_date, price, quantity, created_by, description, modified_by, name, product_code, status)
VALUES
('2024-09-01', '2024-09-01', 99, 10, 'admin', 'Smartphone', 'admin', 'Smartphone XYZ', 'P0001', 'ACTIVE'),
('2024-09-02', '2024-09-02', 19, 50, 'admin', 'T-shirt', 'admin', 'Cool T-shirt', 'P0002', 'ACTIVE'),
('2024-09-03', '2024-09-03', 299, 5, 'admin', 'Washing Machine', 'admin', 'Front Load Washing Machine', 'P0003', 'ACTIVE'),
('2024-09-04', '2024-09-04', 12, 20, 'admin', 'Novel', 'admin', 'Best Selling Novel', 'P0004', 'ACTIVE'),
('2024-09-05', '2024-09-05', 29, 15, 'admin', 'Action Figure', 'admin', 'Superhero Action Figure', 'P0005', 'ACTIVE'),
('2024-09-06', '2024-09-06', 49, 100, 'admin', 'Lipstick', 'admin', 'Matte Lipstick', 'P0006', 'ACTIVE'),
('2024-09-07', '2024-09-07', 199, 30, 'admin', 'Tennis Racket', 'admin', 'Pro Tennis Racket', 'P0007', 'ACTIVE'),
('2024-09-08', '2024-09-08', 14999, 3, 'admin', 'Car Accessories', 'admin', 'Luxury Car Seat', 'P0008', 'ACTIVE'),
('2024-09-09', '2024-09-09', 799, 10, 'admin', 'Sofa', 'admin', 'Leather Sofa', 'P0009', 'ACTIVE'),
('2024-09-10', '2024-09-10', 9, 200, 'admin', 'Pen Set', 'admin', 'Premium Pen Set', 'P0010', 'ACTIVE'),
('2024-09-11', '2024-09-11', 25, 50, 'admin', 'Garden Tools', 'admin', 'Complete Garden Tool Kit', 'P0011', 'ACTIVE'),
('2024-09-12', '2024-09-12', 349, 5, 'admin', 'Guitar', 'admin', 'Electric Guitar', 'P0012', 'ACTIVE'),
('2024-09-13', '2024-09-13', 59, 100, 'admin', 'Vitamins', 'admin', 'Multivitamin Pack', 'P0013', 'ACTIVE'),
('2024-09-14', '2024-09-14', 24, 80, 'admin', 'Dog Food', 'admin', 'Organic Dog Food', 'P0014', 'ACTIVE'),
('2024-09-15', '2024-09-15', 19, 50, 'admin', 'Baby Bottle', 'admin', 'Anti-Colic Baby Bottle', 'P0015', 'ACTIVE');

INSERT INTO product_category (created_date, modified_date, status, category_id, product_id, created_by, modified_by)
VALUES
('2024-09-01', '2024-09-01', 'ACTIVE', 1, 1, 'admin', 'admin'),
('2024-09-02', '2024-09-02', 'ACTIVE', 2, 2, 'admin', 'admin'),
('2024-09-03', '2024-09-03', 'ACTIVE', 3, 3, 'admin', 'admin'),
('2024-09-04', '2024-09-04', 'ACTIVE', 4, 4, 'admin', 'admin'),
('2024-09-05', '2024-09-05', 'ACTIVE', 5, 5, 'admin', 'admin'),
('2024-09-06', '2024-09-06', 'ACTIVE', 6, 6, 'admin', 'admin'),
('2024-09-07', '2024-09-07', 'ACTIVE', 7, 7, 'admin', 'admin'),
('2024-09-08', '2024-09-08', 'ACTIVE', 8, 8, 'admin', 'admin'),
('2024-09-09', '2024-09-09', 'ACTIVE', 9, 9, 'admin', 'admin'),
('2024-09-10', '2024-09-10', 'ACTIVE', 10, 10, 'admin', 'admin'),
('2024-09-11', '2024-09-11', 'ACTIVE', 11, 11, 'admin', 'admin'),
('2024-09-12', '2024-09-12', 'ACTIVE', 12, 12, 'admin', 'admin'),
('2024-09-13', '2024-09-13', 'ACTIVE', 13, 13, 'admin', 'admin'),
('2024-09-14', '2024-09-14', 'ACTIVE', 14, 14, 'admin', 'admin'),
('2024-09-15', '2024-09-15', 'ACTIVE', 15, 15, 'admin', 'admin');


