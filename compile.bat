@echo off
echo Compiling backend files...
javac -d bin backend/user.java
javac -d bin -cp bin backend/student.java
javac -d bin -cp bin backend/vendor.java
javac -d bin -cp bin backend/admin.java
javac -d bin -cp bin backend/canteenOrder.java

echo Compiling GUI files...
javac -d bin -cp bin gui/mainGUI.java
javac -d bin -cp bin gui/loginScreen.java
javac -d bin -cp bin gui/registerScreen.java
javac -d bin -cp bin gui/studentScreen.java
javac -d bin -cp bin gui/vendorScreen.java
javac -d bin -cp bin gui/adminScreen.java
javac -d bin -cp bin gui/placeOrderScreen.java
javac -d bin -cp bin gui/viewOrdersScreen.java
javac -d bin -cp bin gui/cancelOrderScreen.java
javac -d bin -cp bin gui/markOrderDeliveredScreen.java
javac -d bin -cp bin gui/datePicker.java
javac -d bin -cp bin gui/setStudentBalanceScreen.java
echo Compilation complete!
pause