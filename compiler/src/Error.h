#ifndef ERROR_H
#define ERROR_H

#include <string>
using namespace std;

class Error {
public:
	Error();
	~Error();
	static void unRecognised(string str, int line);
	static void lackSymmetrySymbol(char c);
	static void tooLongSymbol(int line);
	static void lackOperator(char c);
	static void noTable(string str);
	static void twoOrMorePKDecl();
	static void PK_noDecl();
	static void tooManyCols();
	static void dupColName();
	static void noEqualValues();
	static void dup_PK();
};

#endif