#ifndef DATA_H
#define DATA_H

#define KEY_NUM 12

#include <string>
#include <vector>
#include <map>
#include <iostream>
using namespace std;

typedef enum {
	KEYWORD = 1, NUM = 2, TYPE = 3, ID = 4,
	LOPERATOR = 5, AOPERATOR = 6, OPERATOR = 7,
	UNRECOGNISED = 8, END = 9, NUL = 10, _EOF = 11
}Type;

typedef enum {
	CREATE, INSERT, DELETE, QUERY
}Stmt;

typedef struct token {
	string val;
	Type type;
	token(string str, Type t) {
		val = str;
		type = t;
	}
}Token;

typedef struct data {
	string tableName;
	map<string, int> cols;
	vector<string> priKeys;
	vector<Token*> condition;
	void clear() {
		tableName = "";
		cols.clear();
		priKeys.clear();
		condition.clear();
	}
	void output() {
		cout << "tableName: " << tableName << endl;
			for (map<string, int>::iterator it = cols.begin(); it != cols.end(); it++) {
				cout << it->first << " " << it->second << endl;
			}
			for (vector<string>::iterator it = priKeys.begin(); it != priKeys.end(); it++)
				cout << "Key: " << (*it) << endl;
			for (vector<Token*>::iterator it = condition.begin(); it != condition.end(); it++)
				cout << (*it)->val;
			cout << endl;
	}
}Data;

const string Keys[KEY_NUM] = {
	"CREATE", "TABLE", "PRIMARY", "KEY", "DEFAULT", "INSERT",
	"INTO", "VALUES", "DELETE", "FROM", "WHERE", "SELECT"
};

#endif
