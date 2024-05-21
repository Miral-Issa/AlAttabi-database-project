drop database project;
create database project; -- DataBase Project
use project;

DROP TABLE IF EXISTS Product;
create table Product( -- inventory warehause
productID int(10) NOT NULL,
name varchar(32) Default NULL,
quantity int(20),
cost int NOT NULL,
arrivedate date,
shelfLife int(10),
SupplierID INT NOT NULL,
primary key(productID)
);

DROP TABLE IF EXISTS Supplier;
create table Supplier( 
supplierID int(10) NOT NULL,
Sname varchar(32) Default NULL,
phoneNumber int(20),
Saddress varchar(30),
primary key(supplierID)
);

DROP TABLE IF EXISTS customer;
create table customer( 
CustomerID int(10) not null,
Cname varchar(30),
phoneNumber int(30),
address varchar(30),
Ctype varchar(20),
primary key(CustomerID)
);



DROP TABLE IF EXISTS orders;
create table orders( 
orderID int(10) not null,
CustomerID int(10) not null,
orderdate date,
requireddate date,
primary key(orderID),
foreign key (CustomerID) references customer(CustomerID) on delete cascade
);

DROP TABLE IF EXISTS orderline;
create table orderline( 
orderID int(10),
productID int(10),
quantity int(20),
unitprice int(10),
primary key(orderID,productID),
foreign key (orderID) references orders(orderID) on delete cascade,
foreign key (productID) references Product(productID) on delete cascade
);
DROP TABLE IF EXISTS purchase_line;
create table purchase_line( 
purID int(10),
productID int(10),
quantity int(20),
cost int(10),
primary key(purID,productID),
foreign key (purID) references PurchaseOrder(purID) on delete cascade,
foreign key (productID) references Product(productID) on delete cascade
);

DROP TABLE IF EXISTS order_payment;
create table order_payment( 
paymentID int(10) not null,
payment_type varchar(20),
payed_amount int(20),
payment_date date,
due_date date default null, -- for cach the same as payment date, for check the date of caching the check, for dept null as we still dont know when the dept will be payed
orderID int(10),
primary key(paymentID),
foreign key (orderID) references orders(orderID) on delete cascade
);

-- related to the suppliers
DROP TABLE IF EXISTS PurchaseOrder;
create table PurchaseOrder( 
purID int(10) NOT NULL,
totalCost int(20) not null,
purDate date,
supplierID int(10),
primary key(purID),
foreign key (supplierID) references Supplier(supplierID) on delete cascade
);
-- select * from PurchaseOrder;
DROP TABLE IF EXISTS purchase_payment;
create table purchase_payment( 
paymentID int(10) NOT NULL,
payment_type varchar(20),
payment_amount int(20),
payment_date date,
due_date date default null, -- for cach the same as payment date, for check the date of caching the check, for dept null as we still dont know when the dept will be payed
purID int(10),
primary key(paymentID),
foreign key (purID) references PurchaseOrder(purID)  on delete cascade
);
-- Insert into order_payment (paymentID, payment_type, paymed_amount, payment_date, due_date,orderID) values(54,'dept',384,'2020-04-07','null', 2);
insert into Product values 
(1,"قمح",25000,25000,"2022-6-1",10,30),(2,"حمص",5000,20000,"2022-7-1",9,34),(3,"زعتر ورق",3000,75000,"2022-5-15",4,34),
(4,"عدس حب",5000,30000,"2023-2-23",6,32),(5,"فاصولياء",3000,21000,"2023-1-4",5,32),(6,"فريكة",3000,19500,"2023-4-3",7,31),
(7,"بهار سادة",1500,80000,"2024-3-6",4,33),(8,"كمون",1000,25000,"2024-10-5",9,31),(9,"يانسون",1300,26000,"2024-1-27",7,31);
insert into Supplier values 
(30,"علاء الفقوعي",056623251, "جنين"),(31,"يوسف الجبعاوي",0562323471, "جنين-جبع"),(32,"عزام الشويكي",056983582 , "الخليل"),
(33,"شركة السعيد",05687423, "الخليل"),(34,"نوفان",056843101, "صانور");
insert into PurchaseOrder values 
(40,25000,"2022-5-25",30),(41,19500+26000+25000,"2023-3-30",31),(42,21000+30000,"2023-2-20",32),
(43,80000,"2024-3-1",33),(44,20000+75000,"2022-5-4",34);
insert into purchase_line values 
(40,1,25000,2500),
(41,6,3000,19500),(41,8,1000,25000),(41,9,1300,26000),
(42,4,5000,30000),(42,5,3000,21000),
(43,7,1500,80000),
(44,2,5000,20000),(44,3,3000,75000);

insert into purchase_payment values 
(50,"cach",20000,"2022-5-27","2022-5-27",40),(51,"check",5000,"2022-5-27","2022-9-27",40),
(52,"debt",19500,"2023-3-30",null,41),(53,"check",26000+25000,"2023-3-30","2024-1-1",41),
(54,"cach",21000+30000,"2023-2-20","2023-2-20",42),
(55,"cach",80000,"2024-3-1","2024-3-1",43),
(56,"debt",20000,"2022-5-4","2022-7-1",44),(57,"check",75000,"2022-5-4","2022-12-4",44);

insert into customer values 
(10,"مطعك يا هلا",056789421,"بيت لحم","مطعم"),(11,"سوبر ماركت ابو عبيدة",05693291,"دار صلاح","سوبر ماركت"),(12,"كراجة",05982391,"حلحول","محل تجاري"),
(13,"محمد ابو سنينة",05956409,"الخليل","محل تجاري"),(14,"يسوبر ماركت خطاب",05623471,"العبيات","سوبر ماركت"),(15,"العبيدية مول",05993732,"االعبيدية","مول");
insert into orders values 
(100,14,"2022-9-5","2022-9-19"),(101,12,"2022-7-13","2023-7-20"),(102,15,"2022-4-23","2023-6-1"),
(103,11,"2023-3-5","2022-3-19"),(104,10,"2023-5-23","2023-5-28"),(105,13,"2024-1-3","2024-1-16");
insert into orderline values 
(100,3,300,6),(100,4,450,9),
(101,8,75,15),(101,3,120,7),(101,9,20,12),
(102,5,340,4),(102,6,43,6),(102,2,203,6),
(103,4,638,9),(103,3,280,6),
(104,8,60,15),(104,9,80,12),
(105,1,1000,6);
insert into order_payment values 
(80,"cach",300*6+450*9,"2022-9-5","2022-9-5",100),
(81,"check",75*15+120*7+20*12,"2023-7-20","2024-3-20",101),
(82,"debt",340*4,"2022-4-23",null,102),(83,"cach",43*6+203*6,"2023-6-1","2023-12-1",102),
(84,"check",638*9+280*6,"2022-3-19","2023-1-1",103),
(85,"debt",60*15+80*12,"2023-5-23","2023-12-1",104),
(86,"cach",630*6,"2024-1-16","2024-1-16",105),(87,"check",370*6,"2024-1-16","2024-6-16",105);


select * from customer,orders,order_payment where customer.CustomerID = orders.CustomerID and orders.orderID = order_payment.orderID and order_payment.payment_type = "dept" and order_payment.due_date is null;
select C.CustomerID, C.Cname ,C.address, C.phoneNumber, C.Ctype from customer C,orders,order_payment where C.CustomerID = orders.CustomerID and orders.orderID = order_payment.orderID  and order_payment.due_date is null;
select op.paymentID,op.payed_amount,op.payment_date,op.due_date,C.CustomerID, C.Cname,C.phoneNumber from order_payment op,customer C,orders where C.CustomerID = orders.CustomerID and orders.orderID = op.orderID and op.payment_type="check";
