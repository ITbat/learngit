#ifndef PARSER_CPP
#define PARSER_CPP

#include <iostream>
#include <stdlib.h>
#include "Parser.h"
using namespace std;

Parser::Parser(FILE *fp) { 
	lex = new Lexer(fp);
	lookahead = lex->getNextToken();
	isError = false;
}

Parser::~Parser() {}

Token* Parser::getLookahead() { return lookahead; }

Stmt Parser::getStmt() { return stmt; }

vector<Token*> Parser::getTokenStream() {
	isError = false;
	token_stream.clear();
	ssql_stmt();
	return token_stream;
}

void Parser::ssql_stmt() {
	if (lookahead->type == _EOF)
		exit(1);
	switch ((lookahead->val)[0]) {
		case 'C' : create_stmt(); stmt = CREATE; break;
		case 'I' : insert_stmt(); stmt = INSERT; break;
		case 'D' : delete_stmt(); stmt = DELETE; break;
		case 'S' : query_stmt(); stmt = QUERY; break;
		default : SyntaxError();
	}
}

void Parser::create_stmt() {
	if (!isError) match("CREATE");
	if (!isError) match("TABLE");
	if (!isError) match("ID");
	if (!isError) match("(");
	if (!isError) decl_list();
	if (!isError) match(")");
	if (!isError) match(";");
}

void Parser::insert_stmt() {
	if (!isError) match("INSERT");
	if (!isError) match("INTO");
	if (!isError) match("ID");
	if (!isError) match("(");
	if (!isError) column_list();
	if (!isError) match(")");
	if (!isError) match("VALUES");
	if (!isError) match("(");
	if (!isError) value_list();
	if (!isError) match(")");
	if (!isError) match(";");
}

void Parser::delete_stmt() {
	if (!isError) match("DELETE");
	if (!isError) match("FROM");
	if (!isError) match("ID");
	if (!isError) where_clause();
	if (!isError) match(";");
}

void Parser::query_stmt() {
	if (!isError) match("SELECT");
	if (!isError) select_list();
	if (!isError) match("FROM");
	if (!isError) match("ID");
	if (!isError) where_clause();
	if (!isError) match(";");
}

void Parser::select_list() {
	if (lookahead->val == "*")
		match("*");
	else
		column_list();
}

void Parser::where_clause() {
	if (lookahead->val == "WHERE") {
		match("WHERE");
		disjunct();
	}
}

void Parser::disjunct() {
	conjunct();
	if (lookahead->val == "||") {
		match("||");
		disjunct();
	}
}

void Parser::conjunct() {
	_bool(); 
	if (lookahead->val == "&&") {
		match("&&");
		conjunct();
	}
}

void Parser::conjunct_list() {
	_bool();
	if (lookahead->val == "&&") {
		match("&&");
		conjunct_list();
	}
}

void Parser::_bool() {
	if (lookahead->val == "(") {
		match("(");
		disjunct();
		match(")");
		return;
	}
	if (lookahead->val == "!") {
		match("!");
		_bool();
		return;
	}
	comp();
}

void Parser::comp() {
	expr();
	rop();
	expr();
}

void Parser::expr() {
	term();
	if (lookahead->val == "+") {
		match("+");
		expr();
		return;
	}
	if (lookahead->val == "-") {
		match("-");
		expr();
		return;
	}
}

void Parser::term() {
	unary();
	if (lookahead->val == "*") {
		match("*");
		term();
		return;
	}
	if (lookahead->val == "/") {
		match("/");
		term();
		return;
	}
}

void Parser::unary() {
	if (lookahead->val == "(") {
		match("(");
		expr();
		match(")");
		return;
	}
	if (lookahead->val == "-") {
		match("-");
		unary();
		return;
	}
	if (lookahead->val == "+") {
		match("+");
		unary();
		return;
	}
	if (lookahead->type == ID) {
		match("ID");
		return;
	}
	if (lookahead->type == NUM) {
		match("NUM");
		return;
	}
	SyntaxError();
}

void Parser::operand() {
	if (lookahead->type == NUM)
		match("NUM");
	else if (lookahead->type == ID)
		match("ID");
}

void Parser::rop() {
	if (lookahead->val == "<>")
		match("<>");
	else if (lookahead->val == "==")
		match("==");
	else if (lookahead->val == ">")
		match(">");
	else if (lookahead->val == "<")
		match("<");
	else if (lookahead->val == ">=")
		match(">=");
	else if (lookahead->val == "<=")
		match("<=");
}

void Parser::decl_list() {
	decl();
	if (lookahead->val == ",") {
		match(",");
		decl_list();
	}
}

void Parser::decl() {
	if (lookahead->type == ID) {
		match("ID");
		match("INT");
		default_spec();
		return;
	}
	if (lookahead->val == "PRIMARY") {
		match("PRIMARY");
		match("KEY");
		match("(");
		column_list();
		match(")");
	}

}

void Parser::default_spec() {
	if (lookahead->val == "DEFAULT") {
		match("DEFAULT");
		match("=");
		simple_expr();
	}
}

void Parser::column_list() {
	match("ID");
	if (lookahead->val == ",") {
		match(",");
		column_list();
	}
}

void Parser::value_list() {
	simple_expr();
	if (lookahead->val == ",") {
		match(",");
		value_list();
	}
}

void Parser::simple_expr() {
	simple_term();
	if (lookahead->val == "+") {
		match("+");
		simple_expr();
		return;
	}
	if (lookahead->val == "-") {
		match("-");
		simple_expr();
		return;
	}
}

void Parser::simple_term() {
	simple_unary();
	if (lookahead->val == "*") {
		match("*");
		simple_term();
		return;
	}
	if (lookahead->val == "/") {
		match("/");
		simple_term();
		return;
	}
}

void Parser::simple_unary() {
	if (lookahead->val == "(") {
		match("(");
		simple_expr();
		match(")");
		return;
	}
	if (lookahead->val == "-") {
		match("-");
		simple_unary();
		return;
	}
	if (lookahead->val == "+") {
		match("+");
		simple_unary();
		return;
	}
	if (lookahead->type == NUM) {
		match("NUM");
		return;
	}
	SyntaxError();
}

void Parser::match(std::string str) {
	if (str == "ID") {
		if (lookahead->type == ID) {
			token_stream.push_back(lookahead);
			lookahead = lex->getNextToken();
		} else {
			SyntaxError();
		}
	} else if (str == "NUM") {
		if (lookahead->type == NUM) {
			token_stream.push_back(lookahead);
			lookahead = lex->getNextToken();
		} else {
			SyntaxError();
		}
	} else if (lookahead->val == str) {
		if (str != "INT" && str != "=" && str != ";"
			&& lookahead->type != KEYWORD)
			token_stream.push_back(lookahead);
		if (str == "KEY" || str == "FROM"  || str == "WHERE")
			token_stream.push_back(lookahead);
		lookahead = lex->getNextToken();
	} else {
		SyntaxError();
	}
}

void Parser::SyntaxError() {
	std::cout << "Syntax error at line "
			<< lex->getLine() << std::endl;
	isError = true;
	token_stream.clear();
	while (lookahead->val != ";" && lookahead->type != _EOF)
		lookahead = lex->getNextToken();
	if (lookahead->type != _EOF)
		lookahead = lex->getNextToken();
}

#endif