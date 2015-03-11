#ifndef TRANSLATOR_H
#define TRANSLATOR_H

#include "Parser.h"
#include "DataBase.h"

class Translator {
public:
	Translator(FILE *fp);
	~Translator();
	void execute();
	void create();
	void insert();
	void query();
	void _delete();
	bool isExist(string str);
private:
	vector<Token*> tokens;
	Data data;
	Parser *par;
	Stmt stmt;
	DataBase *db;
	Calculator *cal;
};

#endif