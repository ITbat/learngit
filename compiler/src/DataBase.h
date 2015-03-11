#ifndef DATABASE_H
#define DATABASE_H

#include "Data.h"
#include "Table.h"

class DataBase {
public:
	DataBase();
	~DataBase();
	bool table_exist(string name);
	void create(string name, map<string,int> cols, vector<string> prikeys);
	void insert(string name, map<string,int> cols);
	void _delete(string name, map<string, int> cols, vector<Token*> tokens);
	void query(string name, map<string,int> cols, vector<Token*> tokens);
	void delete_table(string name);
private:
    vector<Table> table_list;
};

#endif