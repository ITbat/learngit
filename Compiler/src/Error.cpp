#ifndef ERROR_CPP
#define ERROR_CPP

#include <iostream>
#include "Error.h"

Error::Error() {}
Error::~Error() {}

void Error::unRecognised(string str, int line) {
	cout << str << " : " << "unRecognised symbol at line " << line << endl;
}

void Error::tooLongSymbol(int line) {
	cout << "Identifier is too long at line " << line << endl;
}

void Error::twoOrMorePKDecl() {
	cout << "Syntax error: two or more primary key declarations"
		<< endl;
}

void Error::PK_noDecl() {
	cout << "Syntax error: columns in the table do not contain the primary key"
		<< endl;
}

void Error::tooManyCols() {
	cout << "Syntax error: the number of columns in a table should <= 100"
		<< endl;
}

void Error::dupColName() {
	cout << "Syntax error: duplicate column names" << endl;
}

void Error::noEqualValues() {
	cout << "Syntax error: the number of columns should equal to the number of values"
		<< endl;
}

void Error::dup_PK() {
	cout << "Syntax error: duplicate primary keys" << endl;
}

#endif