UC Homework 10 Rodriguez 11/09/2015


*Note* Resubmission. Found a small bug with code generation and declaring variables. fixed.  

I have changed the gammar for this assignment. Highlighted changes below:

1) Variables must be declared before the are used. Format as follows
	declare x Integer;
2) <Id List> is not used for declaring variables, thus one 1 variable per declaration. 
3) Blocks can now be used. Format as follows
	BEGIN
		BEGIN
		END
	END
4) Blocks cannot be empty. 

Program uses symbol table to store variables that have been declared and are being used in the 
current scope. Variables are not redeclared in generated code if the variable has already been declared.
While Symbol table keep track of every declaration of a variable the stringspace keeps only 1 copy 
of any given string. Duplicates are handled at higher levels. 

Attached files include:

hw8input.txt contains the code file to be read by the compiler
hw10input.txt contains micro grammar with action symbols
hw9output.txt contains code generation from compiler
hw9programstatus.txt contains the status information output by the compiler similar to lecture notes. 

Contains Java 8 features. 

All files need to me compiled. Homework.java contains main. 