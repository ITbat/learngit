#ifndef TABLE_H
#define TABLE_H

#include "Data.h"
#include "Calculator.h"

class Table {
public:
	Table();
	Table(string name, map<string,int> cols,vector<string> pk);

	map<string,int> reorder(map<string,int> col);
	bool check(map<string,int> col);   
        
	bool is_exist(map<string,int> col);
	void insert(map<string,int> col);
	void remove(map<string, int> col, vector<Token*> tokens);
	void query(map<string,int> col,vector<Token*> tokens);

	void print_line(int n);
	int len_line(string s);
	void name_print(string s,int length);
	void value_print(int s,int length);

	string getName();    
	int getLength();
                     
private:
    string table_name;
	map<string,int> col_name;
	vector<bool> prikeys;
	int length;
	vector< vector<int> > col_list;
	Calculator *cal;
};

#endif