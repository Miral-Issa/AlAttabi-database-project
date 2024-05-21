module Herbalshop {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	requires mysql.connector.java;
	requires java.sql;
	requires java.desktop;
	
	opens application to javafx.graphics, javafx.fxml, javafx.base;
     exports application;

}
