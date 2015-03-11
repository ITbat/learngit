#ifndef CALCULATOR_CPP
#define CALCULATOR_CPP

#include "Calculator.h"

Calculator::Calculator() { zero_error = false; } 
Calculator::~Calculator() {}

bool Calculator::isZeroError(){return zero_error;}

void Calculator::resetZeroError(bool flag) { zero_error = flag; }

bool Calculator::priority(string a,string b) {
	map<string, int> prio;
	prio.insert(pair<string, int>("(",99));
	prio.insert(pair<string, int>("NEG",2));
	prio.insert(pair<string, int>("*",3));
	prio.insert(pair<string, int>("/",3));
	prio.insert(pair<string, int>("+",4));
	prio.insert(pair<string, int>("-",4));
	prio.insert(pair<string, int>(">",5));
	prio.insert(pair<string, int>("<",5));
	prio.insert(pair<string, int>(">=",5));
	prio.insert(pair<string, int>("<=",5));
	prio.insert(pair<string, int>("==",6));
	prio.insert(pair<string, int>("<>",6));
	prio.insert(pair<string, int>("!",7));
	prio.insert(pair<string, int>("&&",11));
	prio.insert(pair<string, int>("||",12));

	map<string, int>::iterator finda;
	finda = prio.find(a);
	map<string, int>::iterator findb;
	findb = prio.find(b);
	return((finda->second) <= (findb->second));
}

int Calculator::getnum() {
	int num = 0;
	if(!operations.empty())
	{	
		num = operations.top();
		operations.pop();
	}
	return num;
}

bool Calculator::getbool() {
	bool b = true;
	if(!logicresult.empty())
	{	
		b = logicresult.top();
		logicresult.pop();
	}
	return b;
}

void Calculator::calculate() {
	if(operators.top() == "NEG")
		operations.push(0 - getnum());
	if(operators.top() == "+")
		operations.push(getnum() + getnum());
	else if(operators.top()  == "-")
	{
		int numa = getnum();
		int numb = getnum();
		operations.push(numb - numa);
	}
	else if(operators.top()  == "*")
		operations.push(getnum() * getnum());
	else if(operators.top()  == "/")
	{
		int numa = getnum();
		int numb = getnum();
		if(numa != 0)
			operations.push(numb / numa);
		else
			zero_error = true;
	}
	else if(operators.top() == ">")
	{
		int numa = getnum();
		int numb = getnum();
		logicresult.push(numb > numa);
	}
	else if(operators.top() == "<")
	{
		int numa = getnum();
		int numb = getnum();
		logicresult.push(numb < numa);
	}
	else if(operators.top() == ">=")
	{
		int numa = getnum();
		int numb = getnum();
		logicresult.push(numb >= numa);
	}
	else if(operators.top() == "<=")
	{
		int numa = getnum();
		int numb = getnum();
		logicresult.push(numb <= numa);
	}
	else if(operators.top() == "==")
	{
		int numa = getnum();
		int numb = getnum();
		logicresult.push(numb == numa);
	}
	else if(operators.top() == "<>")
	{
		int numa = getnum();
		int numb = getnum();
		logicresult.push(numb != numa);
	}
	else if(operators.top() == "&&")
	{
		logicresult.push(getbool() && getbool());
	}
	else if(operators.top() == "||")
	{
		logicresult.push(getbool() || getbool());
	}
	else if(operators.top() == "!")
	{
		logicresult.push(!getbool());
	}
	operators.pop();
}
int Calculator::toInt(vector<Token*>tokens) {
	//clear stack
	while(!operations.empty())
		operations.pop();
	while(!operators.empty())
		operators.pop();
	while(!logicresult.empty())
		logicresult.pop();
	for (vector<Token*>::iterator it = tokens.begin(); it != (tokens.end());it++) 
	{
		if((*it)->type == NUM)
			operations.push(atoi(((*it)->val).c_str()));
		else if((*it)->type == AOPERATOR)
		{
			if((*it)->val == "-")
			{
				if(it == tokens.begin())
					(*it)->val = "NEG";
				else
				{
					vector<Token*>::iterator pre=it-1;
					if(((*pre)->type == NUM)||((*pre)->type == ID)||(!operators.empty()&&operators.top()==")"))
						(*it)->val = "-";
					else
						(*it)->val = "NEG";
				}
			}
			if(operators.empty())
				operators.push((*it)->val);
			else
			{
				while(!operators.empty()&&priority(operators.top(),(*it)->val))
				{
					calculate();
				}
				operators.push((*it)->val);
			}
		}
		else if((*it)->val == "(")
		{
			operators.push((*it)->val);
		}
		else if((*it)->val == ")")
		{
			while(!operators.empty()&&operators.top()!="(")
			{
				calculate();
			}
			if(!operators.empty())
				operators.pop();
		}

	} 
	while(!operators.empty())
	{
		calculate();
	}
	if(!operations.empty())
		return(operations.top());
	else
		return 0;//no arithmetic input;
}

bool Calculator::cal_condition(vector<string> col_names,vector<int> col_val, vector<Token*> tokens) {
	//clear stack
	while(!operations.empty())
		operations.pop();
	while(!operators.empty())
		operators.pop();
	while(!logicresult.empty())
		logicresult.pop();
	for (vector<Token*>::iterator it = tokens.begin(); it != (tokens.end());it++) 
	{
		if((*it)->type == NUM)
			operations.push(atoi(((*it)->val).c_str()));
		else if((*it)->type == AOPERATOR)
		{
			if((*it)->val == "-")
			{
				if(it == tokens.begin())
					(*it)->val = "NEG";
				else
				{
					vector<Token*>::iterator pre=it-1;
					if(((*pre)->type == NUM)||((*pre)->type == ID)||(!operators.empty()&&operators.top()==")"))
						(*it)->val = "-";
					else
						(*it)->val = "NEG";
				}
			}
			if(operators.empty())
				operators.push((*it)->val);
			else
			{
				while(!operators.empty()&&priority(operators.top(),(*it)->val))
				{
					calculate();
				}
				operators.push((*it)->val);
			}
		}
		else if((*it)->type == LOPERATOR)
		{
			if(operators.empty())
				operators.push((*it)->val);
			else
			{
				while(!operators.empty()&&priority(operators.top(),(*it)->val))
				{
					calculate();
				}
				operators.push((*it)->val);
			}
		}
		else if((*it)->type==ID)
		{
			vector<int>::iterator i;
			i = col_val.begin();
			for(vector<string>::iterator s = col_names.begin();s != col_names.end();s++)
			{
				if((*it)->val == (*s))
				{
					operations.push(*i);
					break;
				}
				i++;
			}
		}
		else if((*it)->val == "(")
		{
			operators.push((*it)->val);
		}
		else if((*it)->val == ")")
		{
			while(!operators.empty()&&operators.top()!="(")
			{
				calculate();
			}
			if(!operators.empty())
				operators.pop();
		}

	} 
	while(!operators.empty())
	{
		calculate();
	}
	if(!logicresult.empty())
		return(logicresult.top());
	else
		return false;//no logical input;
}

#endif