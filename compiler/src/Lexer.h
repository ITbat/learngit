#ifndef LEXER_H
#define LEXER_H

#include "Data.h"

class Lexer {
public:
	Lexer(FILE *_fp);
	~Lexer();
	int isKey(std::string s);
	int isLetter(char c);
	int isDigit(char c);
	void analyse();
	FILE* getFP();
	int getLine();
	Token* getNextToken();
private:
	FILE *fp;		//the input file
	Token *tokPtr;	//a pointer to current token
	int line;		//the number of line in the file
};

#endif
