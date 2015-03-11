#ifndef CALCULATOR_H
#define CALCULATOR_H

#include <algorithm>
#include <stack>
#include <iostream>
#include "Data.h"

class Calculator {
public:
	Calculator();
	~Calculator();
	bool isZeroError();
	void resetZeroError(bool flag);
	bool priority(string a,string b);
	int getnum();
	bool getbool();
	void calculate();
	int toInt(vector<Token*>tokens);
	bool cal_condition(vector<string> col_names,vector<int> col_val,
			vector<Token*> tokens);
private:
	stack<string> operators;
	stack<int> operations;
	stack<bool> logicresult;
	bool zero_error;
};

#endif