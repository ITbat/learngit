#ifndef TRANSLATOR_CPP
#define TRANSLATOR_CPP

#include <iostream>
#include <stdlib.h>
#include "Translator.h"
#include "Error.h"

Translator::Translator(FILE *fp) {
	par = new Parser(fp);
	db = new DataBase();
	cal = new Calculator();
}

Translator::~Translator() {}

void Translator::execute() {
	data.clear();
	tokens = par->getTokenStream();
	stmt = par->getStmt();
	//for (vector<Token*>::iterator cur = tokens.begin(); cur != tokens.end(); cur++) cout << (*cur)->val << " "; cout << endl;
	switch (stmt) {
		case CREATE : create(); break;
		case INSERT : insert(); break;
		case DELETE : _delete(); break;
		case QUERY : query(); break;
	}
}

void Translator::create() {
	if (!tokens.empty()) {
		bool primary = false;
		int ColsNum = 0;
		data.tableName = tokens[0]->val;
		bool idOccur = false;
		for (vector<Token*>::iterator cur = tokens.begin()+1; cur != tokens.end();) {
			if ((*cur)->type == NUM || (*cur)->type == AOPERATOR ||
				((*cur)->val == "(" && idOccur)) {
				vector<Token*>::iterator pre = cur-1;
				while ((*cur)->type == NUM || (*cur)->type == AOPERATOR ||
					(*cur)->val == "(" || (*cur)->val == ")") {
					if ((*cur)->val == ")" && cur == tokens.end()-1)
						break;
					data.condition.push_back((*cur));
					cur++;
				}
				idOccur = false;
				//data.output();
				data.cols[(*pre)->val] = cal->toInt(data.condition);
				cur++;
			} else if ((*cur)->val == "KEY") {
				if (primary) {
					Error::twoOrMorePKDecl();
					data.clear();
					return;
				}
				primary = true;
				cur += 2;
				while ((*cur)->val != ")") {
					bool isExistInCols = false;
					for (map<string, int>::iterator it = data.cols.begin();
						it != data.cols.end(); it++)
						if (it->first == (*cur)->val) {
							isExistInCols = true;
							break;
						}
					if (isExistInCols) {
						if (!isExist((*cur)->val)) {
							data.priKeys.push_back((*cur)->val);
							cur++;
							if ((*cur)->val == ",")
								cur++;
						} else {
							Error::dup_PK();
							data.clear();
							return;
						}
					} else {
						Error::PK_noDecl();
						data.clear();
						return;
					}
				}
			} else if ((*cur)->type == ID) {
				if (ColsNum > 100) {
					Error::tooManyCols();
					data.clear();
					return;
				}
				for (map<string, int>::iterator it = data.cols.begin();
					it != data.cols.end(); it++)
					if (it->first == (*cur)->val) {
						Error::dupColName();
						data.clear();
						return;
					}
				idOccur = true;
				data.cols.insert(pair<string, int>((*cur)->val, 0));
				cur++;
				ColsNum++;
			} else {
				cur++;
			}
		}
		db->create(data.tableName, data.cols, data.priKeys);
	}
}

void Translator::insert() {
	if (!tokens.empty()) {
		int ColsNum = 0;
		int valNum = 0;
		int indexOfCols = 0;
		vector<string> cols;
		int bracketNum = 0;
		data.tableName = tokens[0]->val;
		for (vector<Token*>::iterator cur = tokens.begin()+1; cur != tokens.end();) {
			if ((*cur)->type == NUM || (*cur)->type == AOPERATOR ||
				(((*cur)->val == "(" || (*cur)->val == ")") && bracketNum == 2) ) {
				if (ColsNum == valNum) {
					Error::noEqualValues();
					data.clear();
					return;
				}
				data.condition.clear();
				while ((*cur)->type == NUM || (*cur)->type == AOPERATOR ||
					(*cur)->val == "(" || (*cur)->val == ")") {
					if ((*cur)->val == ")" && cur == tokens.end()-1)
						break;
					data.condition.push_back((*cur));
					cur++;
				}
				//data.output();
				data.cols[cols[indexOfCols]] = cal->toInt(data.condition);
				cur++;
				valNum++;
				indexOfCols++;
			} else if ((*cur)->type == ID) {
				if (ColsNum > 100) {
					data.clear();
					return;
				}
				for (map<string, int>::iterator it = data.cols.begin();
					it != data.cols.end(); it++)
					if (it->first == (*cur)->val) {
						Error::dupColName();
						data.clear();
						return;
					}
				data.cols.insert(pair<string, int>((*cur)->val, 0));
				cols.push_back((*cur)->val);
				cur++;
				ColsNum++;
			} else {
				if ((*cur)->val == "(")
					bracketNum++;
				cur++;
			}
		}
		if (ColsNum != valNum) {
			data.clear();
			return;
		}
		db->insert(data.tableName, data.cols);
	}
}

void Translator::query() {
	if (!tokens.empty()) {
		bool whereOccur = false;
		string con = "";
		for (vector<Token*>::iterator cur = tokens.begin(); cur != tokens.end();) {
			if (((*cur)->type == ID || (*cur)->val == "*") && !whereOccur) {
				data.cols.insert(pair<string, int>((*cur)->val, 0));
				cur++;
			} else if ((*cur)->val == "FROM") {
				data.tableName = (*(++cur))->val;
				cur++;
			} else if ((*cur)->val == "WHERE") {
				whereOccur = true;
				for (cur = cur+1; cur != tokens.end(); cur++)
					data.condition.push_back(*cur);
			} else {
				cur++;
			}
		}
		db->query(data.tableName, data.cols, data.condition);
	}
}

void Translator::_delete() {
	if (!tokens.empty()) {
		string con = "";
		data.tableName = tokens[1]->val;
		if (tokens.size() > 2) {
			for (vector<Token*>::iterator cur = tokens.begin()+3;
					cur != tokens.end(); cur++) {
				data.condition.push_back(*cur);
				if ((*cur)->type == ID)
					data.cols.insert(pair<string, int>((*cur)->val, 0));
			}
				
		}
		db->_delete(data.tableName, data.cols, data.condition);
	}
}

bool Translator::isExist(string str) {
	for (std::vector<string>::iterator it = data.priKeys.begin();
		it != data.priKeys.end(); it++)
		if ((*it) == str)
			return true;
	return false;
}  

#endif
